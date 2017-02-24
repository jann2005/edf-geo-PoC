package com.predix.iot.edf.sqs.messaging.subscriber.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	//private String[] meterIds = { "f58c8259-f767-47a9-9122-4278b3087ed7", "7d1a5b48-1da8-4a6b-92ed-7f4569f0361a" };

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
			registry.addEndpoint("/stomp").setAllowedOrigins("*").withSockJS();
		

	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
	}

}
