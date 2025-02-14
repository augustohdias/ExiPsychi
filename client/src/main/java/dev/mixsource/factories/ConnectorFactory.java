package dev.mixsource.factories;

import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.model.response.LoginResponse;

public class ConnectorFactory {
    public static WebSocketServerConnector createGameConnector(LoginResponse response) {
        return new WebSocketServerConnector(
            "ws://localhost:8080/game", 
            response.getUserToken()
        );
    }
} 