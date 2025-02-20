package dev.mixsource.utility;

import dev.mixsource.model.CharacterModel;
import dev.mixsource.port.input.SessionStorage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveCharacters implements SessionStorage {
    private static final Map<WebSocketSession, CharacterModel> activeCharacters = new ConcurrentHashMap<>();

    @Override
    public Optional<CharacterModel> get(final WebSocketSession key) {
        return Optional.ofNullable(activeCharacters.get(key));
    }

    @Override
    public Set<WebSocketSession> keys() {
        return activeCharacters.keySet();
    }

    @Override
    public List<CharacterModel> values() {
        return new ArrayList<>(activeCharacters.values());
    }

    @Override
    public SessionStorage put(WebSocketSession key, CharacterModel value) {
        activeCharacters.put(key, value);
        return this;
    }

    @Override
    public ActiveCharacters update(final WebSocketSession key, final CharacterModel value) {
        activeCharacters.put(key, value);
        return this;
    }

    @Override
    public ActiveCharacters remove(final WebSocketSession key) {
        activeCharacters.remove(key);
        return this;
    }
}
