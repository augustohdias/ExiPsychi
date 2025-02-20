package dev.mixsource.factories;

import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.model.response.LoginResponse;

public class WebSocketConnectorFactory {
    public static WebSocketServerConnector createWebSocketConnector(LoginResponse loginResponse) {
        return new WebSocketServerConnector("ws://localhost:8080/game", loginResponse.getUserToken());
    }
} 