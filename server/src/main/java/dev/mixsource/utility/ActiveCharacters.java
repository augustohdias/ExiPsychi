package dev.mixsource.utility;

import dev.mixsource.model.Character;
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
    private static final Map<WebSocketSession, Character> activeCharacters = new ConcurrentHashMap<>();

    @Override
    public Optional<Character> get(final WebSocketSession key) {
        return Optional.ofNullable(activeCharacters.get(key));
    }

    @Override
    public Set<WebSocketSession> keys() {
        return activeCharacters.keySet();
    }

    @Override
    public List<Character> values() {
        return new ArrayList<>(activeCharacters.values());
    }

    @Override
    public SessionStorage put(WebSocketSession key, Character value) {
        activeCharacters.put(key, value);
        return this;
    }

    @Override
    public ActiveCharacters update(final WebSocketSession key, final Character value) {
        activeCharacters.put(key, value);
        return this;
    }

    @Override
    public ActiveCharacters remove(final WebSocketSession key) {
        activeCharacters.remove(key);
        return this;
    }
}
