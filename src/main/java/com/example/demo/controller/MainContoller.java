package com.example.demo.controller;

import java.util.Map;
import com.example.demo.scenario.model.CallbackCommand;
import com.example.demo.scenario.model.StartScenarioCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.GenericMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainContoller {
    CommandGateway commandGateway;
    JdbcTemplate jdbcTemplate;

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
    public void scenarioCallback(@PathVariable("workflowId") Long workflowId, @RequestParam("action") String action,
			@RequestParam("message") String message) {
        log.info("Received callback for workflowId: {}, action: {}", workflowId, action);

		Map<String, Object> result = jdbcTemplate.queryForMap("SELECT type from domain_event_entry where aggregate_identifier = " + workflowId);
		String scenarioName = (String) result.get("type");
		String commandName = String.format("%s-%s", scenarioName, action);

        CallbackCommand callbackCommand = new CallbackCommand()
				.setWorkflowId(workflowId)
				.setMessage(message);
        GenericMessage<CallbackCommand> genericMessage = new GenericMessage<>(callbackCommand);
        GenericCommandMessage<CallbackCommand> commandMessage = new GenericCommandMessage<>(genericMessage, commandName);

        commandGateway.send(commandMessage);
    }
}
