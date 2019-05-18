package com.example.demo.handler;

import java.util.concurrent.TimeUnit;
import com.example.demo.handler.model.HandlerBEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HandlerB {

	@QueryHandler
	public String handle(HandlerBEvent event) throws InterruptedException {
		log.info("Workflow: {}, message: {}", event.getWorkflowId(), event.getMessage());

		TimeUnit.SECONDS.sleep(3);

		return event.getMessage() + "--" + getClass().getSimpleName();
	}
}
