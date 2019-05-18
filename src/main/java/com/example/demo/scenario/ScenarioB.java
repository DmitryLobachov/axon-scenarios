package com.example.demo.scenario;

import com.example.demo.handler.model.HandlerAEvent;
import com.example.demo.handler.model.HandlerDEvent;
import com.example.demo.scenario.model.CallbackCommand;
import com.example.demo.scenario.model.StartScenarioCommand;
import com.example.demo.scenario.model.StartScenarioEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Aggregate;

@Slf4j
@Aggregate
public class ScenarioB extends AbstractScenario {

    public ScenarioB() {
    }

    @CommandHandler(commandName = "scenario-b")
    public ScenarioB(StartScenarioCommand startCommand, QueryGateway queryGateway) {
        super(startCommand, queryGateway);
    }

    @CommandHandler(commandName = "ScenarioB-handler-a-action")
    public void handleACallback(CallbackCommand callbackCommand, QueryGateway queryGateway) {
		HandlerDEvent handlerDEvent = new HandlerDEvent()
				.setWorkflowId(workflowId)
				.setMessage(callbackCommand.getMessage());

		queryGateway.query(handlerDEvent, String.class)
				.whenComplete((result, ex) -> {
					log.info("SCENARIO FINISHED (workflow id: {}). Final result is: {}", workflowId, result);
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
