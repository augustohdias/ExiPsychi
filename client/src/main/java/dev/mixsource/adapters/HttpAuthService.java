package dev.mixsource.adapters;

import dev.mixsource.application.AuthService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpAuthService implements AuthService {
    private final HttpClient httpClient;
    private final String loginUrl;

    public HttpAuthService(String loginUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.loginUrl = loginUrl;
    }

    @Override
    public String authenticate(String username, String password) throws Exception {
        String jsonPayload = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("Autenticação falhou, status: " + response.statusCode());
        }
    }
} 