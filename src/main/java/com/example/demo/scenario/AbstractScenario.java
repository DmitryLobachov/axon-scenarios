package com.example.demo.scenario;

import com.example.demo.scenario.model.StartScenarioCommand;
import com.example.demo.scenario.model.StartScenarioEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.queryhandling.QueryGateway;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;


@Slf4j
@FieldDefaults(level = AccessLevel.PROTECTED)
abstract public class AbstractScenario {
    @AggregateIdentifier
    Long workflowId;
    QueryGateway queryGateway;

    protected AbstractScenario(StartScenarioCommand startCommand, QueryGateway queryGateway) {
        this.queryGateway = queryGateway;

        StartScenarioEvent startScenarioEvent = new StartScenarioEvent()
                .setWorkflowId(startWorkflow())
                .setMessage(startCommand.getMessage());

        apply(startScenarioEvent)
                .andThen(startScenario(startScenarioEvent));
    }

    abstract protected Runnable startScenario(StartScenarioEvent startingEvent);

    @EventSourcingHandler
    public void initWorkflow(StartScenarioEvent startingEvent) {
        workflowId = startingEvent.getWorkflowId();
    }

    private Long startWorkflow() {
        return System.currentTimeMillis();
    }
}
