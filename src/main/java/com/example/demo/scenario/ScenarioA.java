package com.example.demo.scenario;

import java.util.concurrent.CompletableFuture;
import com.example.demo.handler.model.*;
import com.example.demo.scenario.model.CallbackCommand;
import com.example.demo.scenario.model.StartScenarioCommand;
import com.example.demo.scenario.model.StartScenarioEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Aggregate;

@Slf4j
@Aggregate
public class ScenarioA extends AbstractScenario {

    public ScenarioA() {
    }

    @CommandHandler(commandName = "scenario-a")
    public ScenarioA(StartScenarioCommand startCommand, QueryGateway queryGateway) {
        super(startCommand, queryGateway);
    }

    @CommandHandler(commandName = "ScenarioA-handler-a-action")
    public void handleACallback(CallbackCommand callbackCommand, QueryGateway queryGateway) {
		Long workflowId = callbackCommand.getWorkflowId();

		HandlerBEvent handlerBEvent = new HandlerBEvent()
				.setWorkflowId(workflowId)
				.setMessage(callbackCommand.getMessage());
		CompletableFuture<String> handlerBFuture = queryGateway.query(handlerBEvent, String.class);

		HandlerCEvent handlerCEvent = new HandlerCEvent()
				.setWorkflowId(workflowId)
				.setMessage(callbackCommand.getMessage());
		CompletableFuture<String> handlerCFuture = queryGateway.query(handlerCEvent, String.class);

		CompletableFuture.allOf(handlerBFuture, handlerCFuture)
				.whenComplete((voidResult, throwable) -> {
					try {
						String handlerBResult = handlerBFuture.get();
						String handlerCResult = handlerCFuture.get();

						HandlerDEvent handlerDEvent = new HandlerDEvent()
								.setWorkflowId(workflowId)
								.setMessage(handlerBResult + "_____" + handlerCResult);

						queryGateway.query(handlerDEvent, String.class)
								.whenComplete((result, ex) -> {
									log.info("SCENARIO FINISHED (workflow id: {}). Final result is: {}", workflowId, result);
								});
					} catch (Exception e) {
						log.info("Failed to get result", e);
						throw new RuntimeException(e);
					}
				});
	}

    @Override
    protected Runnable startScenario(StartScenarioEvent startingEvent) {
        return () -> {
            HandlerAEvent handlerAEvent = new HandlerAEvent()
                    .setWorkflowId(startingEvent.getWorkflowId())
                    .setMessage(startingEvent.getMessage());

            queryGateway.query(handlerAEvent, Object.class);
        };
    }
}
