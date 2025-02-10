package dev.mixsource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import dev.mixsource.usecase.GameHandlerUseCase;

@SpringBootApplication
@EnableWebSocket
public class GameServer implements WebSocketConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(GameServer.class, args);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameHandlerUseCase(), "/game").setAllowedOrigins("*");
    }
}
