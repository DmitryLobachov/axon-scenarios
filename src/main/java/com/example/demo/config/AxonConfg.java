package com.example.demo.config;

import org.axonframework.commandhandling.AsynchronousCommandBus;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AxonConfg {

	@Bean
	public AsynchronousCommandBus commandBus(TransactionManager txManager, AxonConfiguration axonConfiguration) {
		AsynchronousCommandBus commandBus = AsynchronousCommandBus.builder()
						.transactionManager(txManager)
						.messageMonitor(axonConfiguration.messageMonitor(CommandBus.class, "commandBus"))
						.build();
		commandBus.registerHandlerInterceptor(
				new CorrelationDataInterceptor<>(axonConfiguration.correlationDataProviders())
		);

		return commandBus;
	}
}
