package com.example.demo.scenario;

import com.example.demo.handler.model.HandlerAEvent;
import com.example.demo.scenario.model.StartScenarioCommand;
import com.example.demo.scenario.model.StartScenarioEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Aggregate;

@Slf4j
@Aggregate
public class ScenarioA extends AbstractScenario {

    @CommandHandler(commandName = "scenario-a")
    public ScenarioA(StartScenarioCommand startCommand, QueryGateway queryGateway) {
        super(startCommand, queryGateway);
    }

    @Override
    protected Runnable startScenario(StartScenarioEvent startingEvent) {
        return () -> {
            HandlerAEvent handlerAEvent = new HandlerAEvent()
                    .setWorkflowId(startingEvent.getWorkflowId())
                    .setMessage(startingEvent.getMessage());

            queryGateway.query(handlerAEvent, Object.class)
                    .whenComplete((result, exception) -> {
                        log.info("Query completed, result: {}, exception: {}", result, exception);
                    });
        };
    }
}
