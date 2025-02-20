package dev.mixsource.config;

import dev.mixsource.config.security.JwtUtils;
import dev.mixsource.port.input.UserMessageWebSocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@Configuration
@AllArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final UserMessageWebSocketHandler userMessageWebSocketHandler;

    private final JwtUtils jwtUtils;

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(userMessageWebSocketHandler, "/game")
                .setAllowedOrigins("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
                        List<String> authHeaders = request
                                .getHeaders()
                                .get("Authorization");
                        
                        if (authHeaders == null || authHeaders.isEmpty()) {
                            response.setStatusCode(HttpStatusCode.valueOf(401));
                            return false;
                        }

                        String token = authHeaders.get(0)
                                .replace("Bearer ", "");
                        
                        if (jwtUtils.validateToken(token)) {
                            return true;
                        }
                        response.setStatusCode(HttpStatusCode.valueOf(401));
                        return false;
                    }

                    @Override
                    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
                    }
                });
    }
}
