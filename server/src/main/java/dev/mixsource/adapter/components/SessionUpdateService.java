package dev.mixsource.adapter.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mixsource.model.Action;
import dev.mixsource.model.CharacterModel;
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
            CharacterModel currentCharacter = activeCharacters.get(session).get();
            
            boolean positionChanged = false;
            
            if (Action.MOVE.equals(currentCharacter.getAction())) {
                double speed = 5.0 * deltaTime;
                switch (currentCharacter.getDirection()) {
                    case UP -> {
                        currentCharacter.setY(currentCharacter.getY() + speed);
                        positionChanged = true;
                    }
                    case DOWN -> {
                        currentCharacter.setY(currentCharacter.getY() - speed);
                        positionChanged = true;
                    }
                    case RIGHT -> {
                        currentCharacter.setX(currentCharacter.getX() + speed);
                        positionChanged = true;
                    }
                    case LEFT -> {
                        currentCharacter.setX(currentCharacter.getX() - speed);
                        positionChanged = true;
                    }
                }
            } else if (Action.IDLE.equals(currentCharacter.getAction())) {
                positionChanged = false;
            }

            if (positionChanged) {
                characters.updateXAndYById(
                    currentCharacter.getId(),
                    currentCharacter.getX(),
                    currentCharacter.getY()
                );
            }

            final List<CharacterModel> broadcastUpdates = new ArrayList<>(activeCharacters.values().stream()
                    .filter(c -> !c.getId().equals(currentCharacter.getId()))
                    .filter(c -> filterByDistance(currentCharacter, c))
                    .toList());
            broadcastUpdates.add(currentCharacter);
            
            try {
                final TextMessage message = new TextMessage(mapper.writeValueAsString(broadcastUpdates));
                session.sendMessage(message);
            } catch (JsonProcessingException ignored) {
                activeCharacters.remove(session);
                System.out.println("Erro ao processar mensagem: " + ignored.getMessage());
            } catch (IOException ignored) {
                activeCharacters.remove(session);
                System.out.println("Erro ao enviar mensagem: " + ignored.getMessage());
            }
        }
    }

    private boolean filterByDistance(final CharacterModel referenceCharacter, final CharacterModel other) {
        final double refX = referenceCharacter.getX();
        final double refY = referenceCharacter.getY();
        final double othX = other.getX();
        final double othY = other.getY();

        final double hypotenuse = Math.hypot((othX - refX), (othY - refY));
        return hypotenuse < 50;
    }

    @PreDestroy
    public void stopScheduler() {
        System.out.println("Parando scheduler");
        scheduler.shutdown();
    }
}

