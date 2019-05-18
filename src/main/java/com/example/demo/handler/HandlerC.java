package com.example.demo.handler;

import com.example.demo.handler.model.HandlerCEvent;
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
public class HandlerC {

	@QueryHandler
	public String handle(HandlerCEvent event) {
		log.info("Workflow: {}, message: {}", event.getWorkflowId(), event.getMessage());

		return event.getMessage() + "--" + getClass().getSimpleName();
	}
}
