package com.example.demo.bus;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.collections4.MapUtils;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import com.example.demo.scenario.model.CallbackCommand;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.axonframework.commandhandling.*;
import org.axonframework.common.Registration;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.monitoring.MessageMonitor;
import org.axonframework.monitoring.MessageMonitor.MonitorCallback;
import org.axonframework.monitoring.NoOpMessageMonitor;
import org.springframework.jdbc.core.JdbcTemplate;
import static java.lang.String.format;
import static org.axonframework.commandhandling.GenericCommandResultMessage.asCommandResultMessage;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SmartCommandBus extends SimpleCommandBus {
	MessageMonitor<? super CommandMessage<?>> messageMonitor;
	JdbcTemplate jdbcTemplate;
	ConcurrentMap<String, Set<MessageHandler<? super CommandMessage<?>>>> subscriptions = new ConcurrentHashMap<>();

	protected SmartCommandBus(Builder builder) {
		super(builder);
		this.messageMonitor = builder.messageMonitor;
		this.jdbcTemplate = builder.jdbcTemplate;

	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	protected <C, R> void doDispatch(CommandMessage<C> command, CommandCallback<? super C, ? super R> callback) {
		MonitorCallback monitorCallback = messageMonitor.onMessageIngested(command);
		MessageHandler<? super CommandMessage<?>> handler = null;

		if (command.getPayload() instanceof CallbackCommand) {
			Long workflowId = ((CallbackCommand) command.getPayload()).getWorkflowId();

			Set<MessageHandler<? super CommandMessage<?>>> handlers = subscriptions.get(command.getCommandName());
			Map<String, Object> result = jdbcTemplate.queryForMap("SELECT type from domain_event_entry where aggregate_identifier = " + workflowId);
			if (MapUtils.isNotEmpty(result)) {
				String type = (String) result.get("type");
				System.err.println();
			}
		} else {
			Set<MessageHandler<? super CommandMessage<?>>> handlers = subscriptions.get(command.getCommandName());
			if (isNotEmpty(handlers)) {
				handler = handlers.iterator().next();
			}
		}

		if (handler != null) {
			handle(command, handler, new MonitorAwareCallback<>(callback, monitorCallback));
		} else {
			String message = format("No handler was subscribed to command [%s]", command.getCommandName());
			NoHandlerForCommandException exception = new NoHandlerForCommandException(message);

			monitorCallback.reportFailure(exception);
			callback.onResult(command, asCommandResultMessage(exception));
		}
	}

	@Override
	public Registration subscribe(String commandName, MessageHandler<? super CommandMessage<?>> handler) {
		subscriptions.compute(commandName, (k, v) -> {
			if (v == null) {
				return Sets.newHashSet(handler);
			} else {
				v.add(handler);
				return v;
			}
		});

		return () -> {
			subscriptions.computeIfPresent(commandName, (k, v) -> {
				v.remove(handler);
				return v.isEmpty() ? null : v;
			});

			return true;
		};
	}

	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class Builder extends SimpleCommandBus.Builder {
		MessageMonitor<? super CommandMessage<?>> messageMonitor = NoOpMessageMonitor.INSTANCE;
		JdbcTemplate jdbcTemplate;

		@Override
		public SimpleCommandBus.Builder messageMonitor(MessageMonitor<? super CommandMessage<?>> messageMonitor) {
			this.messageMonitor = messageMonitor;
			return super.messageMonitor(messageMonitor);
		}

		public Builder jdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
			return this;
		}

		@Override
		public SimpleCommandBus build() {
			return new SmartCommandBus(this);
		}

		@Override
		protected void validate() {
			super.validate();
			if (jdbcTemplate == null) {
				throw new IllegalStateException("jdbc template is not specified");
			}
		}
	}
}
