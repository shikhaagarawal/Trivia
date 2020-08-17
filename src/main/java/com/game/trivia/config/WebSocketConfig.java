package com.game.trivia.config;

import com.game.trivia.util.AppConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint(AppConstants.WEBSOCKET_PATH)
                .setHandshakeHandler(new AssignPrincipalHandshakeHandler())
                .setAllowedOrigins(AppConstants.ALLOWED_ORIGINS)
                .withSockJS();
    }

    /**
     * Creates the in-memory message broker with destination. ALso define prefix api to filter destination
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(AppConstants.BROKER_DESTINATION_PREFIX);
        config.setApplicationDestinationPrefixes(AppConstants.APPLICATION_DESTINATION_PREFIX);
    }
}
