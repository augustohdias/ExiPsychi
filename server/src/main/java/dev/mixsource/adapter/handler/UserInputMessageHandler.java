package dev.mixsource.adapter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.model.Action;
import dev.mixsource.model.Character;
import dev.mixsource.port.input.UserInput;
import dev.mixsource.port.output.repository.Characters;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class UserInputMessageHandler extends TextWebSocketHandler implements UserInput {
    private static final int GRID_SIZE = 20;

    private static final Map<WebSocketSession, Character> activePlayerSessions = new ConcurrentHashMap<>();

    private final Characters characters;

    private final ObjectMapper mapper;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final Character character = mapper.readValue(message.getPayload(), Character.class);
        if (Action.CONNECT.equals(character.getAction())) {
            activePlayerSessions.put(session, character);
        } else if (Action.MOVE.equals(character.getAction())) {
            activePlayerSessions.put(session, character);
            this.characters.updateXAndYById(character.getId(), character.getX(), character.getY());
        }
        broadcastPositions();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        activePlayerSessions.remove(session);
        try {
            broadcastPositions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Player disconnected.");
    }

    private void broadcastPositions() throws Exception {
        final String playersUpdate = mapper.writeValueAsString(new ArrayList<>(activePlayerSessions.values()));
        for (WebSocketSession session : activePlayerSessions.keySet()) {
            session.sendMessage(new TextMessage(playersUpdate));
        }
    }
}
