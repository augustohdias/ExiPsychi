package dev.mixsource.adapter.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.model.Action;
import dev.mixsource.model.Character;
import dev.mixsource.model.Direction;
import dev.mixsource.port.input.SessionStorage;
import dev.mixsource.port.output.repository.Characters;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionUpdateService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ObjectMapper mapper;

    private final SessionStorage activeCharacters;

    private final Characters characters;

    private long lastUpdate = System.currentTimeMillis();

    @PostConstruct
    public void startUpdatingSessions() {
        scheduler.scheduleAtFixedRate(this::updateSessions, 0, 30, TimeUnit.MILLISECONDS);
    }

    private void updateSessions() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdate) / 1000f;
        lastUpdate = currentTime;

        for (WebSocketSession session : activeCharacters.keys()) {
            System.out.println("Atualizando posições...");
            Character currentCharacter = activeCharacters.get(session).get();
            
            if (Action.MOVE.equals(currentCharacter.getAction())) {
                double speed = 5.0 * deltaTime; // Mesma velocidade do cliente
                switch (currentCharacter.getDirection()) {
                    case UP -> currentCharacter.setY(currentCharacter.getY() + speed);
                    case DOWN -> currentCharacter.setY(currentCharacter.getY() - speed);
                    case RIGHT -> currentCharacter.setX(currentCharacter.getX() + speed);
                    case LEFT -> currentCharacter.setX(currentCharacter.getX() - speed);
                }
            }
            
            // Atualizar posição no banco de dados
            characters.updateXAndYById(
                currentCharacter.getId(),
                currentCharacter.getX(),
                currentCharacter.getY()
            );
            
            final List<Character> updates = new ArrayList<>(activeCharacters.values().stream()
                    .filter(c -> !c.getId().equals(currentCharacter.getId()))
                    .filter(c -> filterByDistance(currentCharacter, c))
                    .toList());
            updates.add(currentCharacter);
            try {
                final TextMessage message = new TextMessage(mapper.writeValueAsString(updates));
                session.sendMessage(message);
            } catch (JsonProcessingException ignored) {
                // ignored
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    private boolean filterByDistance(final Character referenceCharacter, final Character other) {
        final double refX = referenceCharacter.getX();
        final double refY = referenceCharacter.getY();
        final double othX = other.getX();
        final double othY = other.getY();

        final double hypotenuse = Math.hypot((othX - refX), (othY - refY));
        return hypotenuse < 50;
    }

    @PreDestroy
    public void stopScheduler() {
        scheduler.shutdown();
    }
}

