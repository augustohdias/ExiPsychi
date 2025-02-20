package dev.mixsource.factories;

import dev.mixsource.adapters.WebSocketServerConnector;

public class ConnectorFactory {
    public static WebSocketServerConnector createGameConnector(final String userToken) {
        return new WebSocketServerConnector(
            "ws://localhost:8080/game", 
            userToken
        );
    }
} 