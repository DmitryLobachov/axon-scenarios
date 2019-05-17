package com.example.demo.controller;

import com.example.demo.scenario.model.CallbackCommand;
import com.example.demo.scenario.model.StartScenarioCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.GenericMessage;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainContoller {
    CommandGateway commandGateway;

    @GetMapping("/scenarios/{name}")
    public ModelMap runScenario(@PathVariable("name") String scenarioName, @RequestParam("message") String message) {
        StartScenarioCommand startCommand = new StartScenarioCommand()
                .setMessage(message);
        GenericMessage<StartScenarioCommand> genericMessage = new GenericMessage<>(startCommand);
        GenericCommandMessage<StartScenarioCommand> commandMessage = new GenericCommandMessage<>(genericMessage, scenarioName);

        Long workflowId = commandGateway.sendAndWait(commandMessage);
        return new ModelMap("id", workflowId);
    }

    @GetMapping("/scenarios/callback/{workflowId}")
    public void scenarioCallback(@PathVariable("workflowId") Long workflowId, @RequestParam("action") String action) {
        log.info("Received callback for workflowId: {}, action: {}", workflowId, action);

        CallbackCommand callbackCommand = new CallbackCommand()
                .setWorkflowId(workflowId);
        GenericMessage<CallbackCommand> genericMessage = new GenericMessage<>(callbackCommand);
        GenericCommandMessage<CallbackCommand> commandMessage = new GenericCommandMessage<>(genericMessage, action);

        commandGateway.send(commandMessage);
    }
}
