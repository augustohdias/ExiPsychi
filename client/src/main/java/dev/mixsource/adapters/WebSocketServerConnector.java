package dev.mixsource.adapters;

import dev.mixsource.application.ServerConnector;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.badlogic.gdx.Gdx;

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

    public boolean isConnected() {
        return client != null && client.isOpen();
    }  

    @Override
    public void connect() {
        try {
            client = new WebSocketClient(new URI(serverUri), httpHeaders) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Gdx.app.log(this.getClass().getName(), "Conexão WebSocket estabelecida com sucesso!");
                }

                @Override
                public void onMessage(String message) {
                    if (updateHandler != null) {
                        updateHandler.accept(message);
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Gdx.app.log(this.getClass().getName(),"Conexão encerrada.");
                }

                @Override
                public void onError(Exception e) {
                    Gdx.app.log(this.getClass().getName(),"Erro na conexão: " + e.getMessage());
                    e.printStackTrace();
                }
            };
            client.connect();
        } catch (Exception e) {
            Gdx.app.error(this.getClass().getName(),"Erro na conexão: " + e.getMessage());
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
            Gdx.app.log(this.getClass().getName(),"WebSocket não está conectado.");
        }
    }
} 