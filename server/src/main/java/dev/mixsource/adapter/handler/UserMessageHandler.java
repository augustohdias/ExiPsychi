package dev.mixsource.adapter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.config.security.JwtUtils;
import dev.mixsource.model.Action;
import dev.mixsource.model.CharacterModel;
import dev.mixsource.model.Command;
import dev.mixsource.model.UserInput;
import dev.mixsource.model.entity.CharacterEntity;
import dev.mixsource.port.input.SessionStorage;
import dev.mixsource.port.input.UserMessageWebSocketHandler;
import dev.mixsource.port.output.repository.Characters;
import dev.mixsource.port.output.repository.Users;
import lombok.AllArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserMessageHandler extends TextWebSocketHandler implements UserMessageWebSocketHandler {
    private final SessionStorage activeCharacters;

    private final Characters characters;

    private final Users users;

    private final ObjectMapper mapper;

    private final JwtUtils jwtUtils;

    private static final String CONNECT_CHARACTER = "CONNECT_CHARACTER";

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        final String payload = message.getPayload();
        if (payload.startsWith(CONNECT_CHARACTER)) {
            final String[] parts = payload.split(" ");
            final Long characterId = Long.parseLong(parts[1]);
            handleCharacterConnection(session, characterId);
            return;
        }
        final UserInput input = mapper.readValue(payload, UserInput.class);
        handleInput(session, input);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        System.out.println("Session closed.");
        activeCharacters.remove(session);
    }

    private void handleInput(final WebSocketSession session, final UserInput input) {
        System.out.println("Recebido input: " + input.getIssuedCommands());
        System.out.println("Map: " + activeCharacters.get(session));
        final CharacterModel character = activeCharacters.get(session).get();
        System.out.println("Personagem ativo: " + character);
        final List<Command> commandsIssued = input.getIssuedCommands();
        
        commandsIssued.forEach(command -> {
            if (command.getCommandType() == Command.CommandType.MOVE) {
                character.setAction(Action.MOVE);
                System.out.println("Movendo personagem: " + character.getAction());
                character.setDirection(command.getDirection());
            } else if (command.getCommandType() == Command.CommandType.STOP) {
                character.setAction(Action.IDLE);
                character.setDirection(null);
            }
        });
        // Atualizar estado do personagem
        activeCharacters.update(session, character);
    }


    private void handleCharacterConnection(final WebSocketSession session, final Long characterId) {
        final List<String> authHeaders = session
                .getHandshakeHeaders()
                .get("Authorization");
        
        if (authHeaders == null || authHeaders.isEmpty()) {
            closeSessionWithError(session);
            return;
        }

        final String token = authHeaders.get(0)
                .replace("Bearer ", "");
        final String username = jwtUtils.getUsernameFromToken(token);
        final long userId = users.findByUsername(username).orElseThrow(() -> new RuntimeException("404")).getId();
        final boolean isValid = characters
                .findAllByUserId(userId)
                .stream()
                .map(CharacterEntity::getId)
                .collect(Collectors.toSet())
                .contains(characterId)
                && !activeCharacters
                        .values()
                        .stream()
                        .map(CharacterModel::getId)
                        .collect(Collectors.toSet())
                        .contains(characterId);
        if (isValid) {
            Optional<CharacterModel> maybeCharacter = characters.findById(characterId).map(c -> c.toCharacter());
            activeCharacters.put(session, maybeCharacter.get());
            return;
        }
        try {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        } catch (IOException ignored) {}
    }

    private void closeSessionWithError(WebSocketSession session) {
        try {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        } catch (IOException ignored) {}
    }
}
