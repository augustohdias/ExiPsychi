package dev.mixsource.port.input;

import dev.mixsource.model.Character;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SessionStorage {
    Optional<Character> get(final WebSocketSession key);

    Set<WebSocketSession> keys();

    List<Character> values();

    SessionStorage put(final WebSocketSession key, final Character value);

    SessionStorage update(final WebSocketSession key, final Character value);

    SessionStorage remove(final WebSocketSession key);
}
