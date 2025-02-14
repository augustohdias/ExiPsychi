package dev.mixsource.adapters;

import dev.mixsource.application.ServerConnector;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WebSocketServerConnector implements ServerConnector {
    private WebSocketClient client;
    private Consumer<String> updateHandler;
    private final String serverUri;
    private final Map<String, String> httpHeaders;

    public WebSocketServerConnector(String serverUri, String token) {
        this.serverUri = serverUri;
        httpHeaders = new HashMap<>();
        httpHeaders.put("Authorization", "Bearer " + token);
    }

    @Override
    public void connect() {
        try {
            client = new WebSocketClient(new URI(serverUri), httpHeaders) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Conexão WebSocket estabelecida com sucesso!");
                }

                @Override
                public void onMessage(String message) {
                    if (updateHandler != null) {
                        updateHandler.accept(message);
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("Conexão encerrada.");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("Erro na conexão: " + e.getMessage());
                }
            };
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnUpdate(Consumer<String> updateHandler) {
        this.updateHandler = updateHandler;
    }

    public void sendMessage(String message) {
        if (client != null && client.isOpen()) {
            client.send(message);
        } else {
            System.out.println("WebSocket não está conectado.");
        }
    }
} 