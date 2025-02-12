package dev.mixsource.config;

import dev.mixsource.config.security.JwtUtils;
import dev.mixsource.port.input.UserInput;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Configuration
@AllArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final UserInput userInput;
    private final JwtUtils jwtUtils;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(userInput, "/game")
                .setAllowedOrigins("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        String token = request.getHeaders().get("Authorization").get(0).replace("Bearer ", "");
                        if (jwtUtils.validateToken(token)) {
                            return true;
                        }
                        response.setStatusCode(HttpStatusCode.valueOf(401));
                        return false;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
                    }
                });
    }
}
