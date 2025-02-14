package dev.mixsource.factories;

import dev.mixsource.adapters.WebSocketServerConnector;
import dev.mixsource.application.AuthService;
import dev.mixsource.model.Character;
import dev.mixsource.model.response.LoginResponse;
import dev.mixsource.ui.CharacterSelectionScreen;
import dev.mixsource.ui.GameScreen;
import dev.mixsource.ui.LoginScreen;

import java.util.function.Consumer;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScreenFactory {
    public static LoginScreen createLoginScreen(AuthService authService, Consumer<LoginResponse> onLoginSuccess) {
        return new LoginScreen(authService, onLoginSuccess);
    }

    public static CharacterSelectionScreen createCharacterSelectionScreen(LoginResponse loginResponse, Consumer<Character> listener) {
        return new CharacterSelectionScreen(
            loginResponse.getCharacters().stream().toList(),
            listener
        );
    }

    public static GameScreen createGameScreen(Character character, WebSocketServerConnector connector) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return new GameScreen(character, connector);
    }
} 