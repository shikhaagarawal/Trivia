package com.game.trivia.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Assign a random username as principal for each websocket client. This is
 * needed to be able to communicate with a specific client.
 */
public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {
    private static final String ATTR_PRINCIPAL = "__principal__";

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final String name;
        attributes.putIfAbsent(ATTR_PRINCIPAL, UUID.randomUUID().toString());
        name = (String) attributes.get(ATTR_PRINCIPAL);
        return new Principal() {
            @Override
            public String getName() {
                return name;
            }
        };
    }

}