package com.example.demo.scenario;

import com.example.demo.handler.model.HandlerAEvent;
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

    @CommandHandler(commandName = "handler-a")
    public void handleACallback(CallbackCommand callbackCommand, QueryGateway queryGateway) {
        log.info("callback for workflow: {}", callbackCommand.getWorkflowId());
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
