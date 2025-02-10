package dev.mixsource.usecase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import dev.mixsource.port.GameHandler;

public class GameHandlerUseCase extends TextWebSocketHandler implements GameHandler {
    private static final int GRID_SIZE = 20;
    private static final Set<String> occupiedPositions = new HashSet<>();
    private static final Map<WebSocketSession, String> playerPositions = new ConcurrentHashMap<>();
    private static final AtomicInteger playerCounter = new AtomicInteger(0);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String position = assignUniquePosition();
        playerPositions.put(session, position);

        // Envia a posição inicial para o jogador
        session.sendMessage(new TextMessage("START " + position));
        broadcastPositions();

        System.out.println("Player " + playerCounter.incrementAndGet() + " joined at position: " + position);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String[] parts = message.getPayload().split(" ");
        if (parts[0].equals("MOVE")) {
            String newPosition = parts[1];

            // Atualiza a posição do jogador
            playerPositions.put(session, newPosition);
            broadcastPositions();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        playerPositions.remove(session);
        try {
            broadcastPositions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Player disconnected.");
    }

    private void broadcastPositions() throws Exception {
        StringBuilder positionsData = new StringBuilder("UPDATE");
        for (Map.Entry<WebSocketSession, String> entry : playerPositions.entrySet()) {
            positionsData.append(" ").append(entry.getValue());
        }
        for (WebSocketSession session : playerPositions.keySet()) {
            session.sendMessage(new TextMessage(positionsData.toString()));
        }
    }

    private String assignUniquePosition() {
        for (int x = 0; x <= GRID_SIZE; x++) {
            for (int y = 0; y <= GRID_SIZE; y++) {
                String pos = x + "," + y;
                if (!occupiedPositions.contains(pos)) {
                    occupiedPositions.add(pos);
                    return pos;
                }
            }
        }
        return "0,0"; // Fallback, mas idealmente nunca acontece
    }
}
