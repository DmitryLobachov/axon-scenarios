package com.example.demo.handler;

import java.util.concurrent.TimeUnit;
import com.example.demo.handler.model.HandlerAEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HandlerA {
    RestTemplate restTemplate = new RestTemplate();

    @QueryHandler
    public Object handle(HandlerAEvent event) {
        new Thread(() -> {
            log.info("Started async operation A");

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                log.error("Failed to wait", e);
                throw new RuntimeException(e);
            } finally {
                log.info("Going to do callback");

                try {
                    String url = String.format("http://localhost:8080/scenarios/callback/%d?action=handler-a", event.getWorkflowId());
                    restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Void>() {});

                    log.info("Callback successfuly sent");
                } catch(Exception e) {
                    log.error("Failed to send callback", e);
                }
            }
        }).start();

        return new Object();
    }
}
