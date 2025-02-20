package dev.mixsource.port.input;

import dev.mixsource.model.CharacterModel;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SessionStorage {
    Optional<CharacterModel> get(final WebSocketSession key);

    Set<WebSocketSession> keys();

    List<CharacterModel> values();

    SessionStorage put(final WebSocketSession key, final CharacterModel value);

    SessionStorage update(final WebSocketSession key, final CharacterModel value);

    SessionStorage remove(final WebSocketSession key);
}
