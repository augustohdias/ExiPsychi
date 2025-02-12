package dev.mixsource.adapter.controller;

import dev.mixsource.model.request.LoginRequest;
import dev.mixsource.model.response.LoginResponse;
import dev.mixsource.port.input.Login;
import dev.mixsource.port.input.LoginFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController implements Login {
    @Autowired
    private final LoginFacade facade;

    @Override
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody final LoginRequest request) {
        try {
            final LoginResponse response = facade.performLogin(request);
            return ResponseEntity.ok(response);
        } catch (final RuntimeException e) {
            return switch (e.getMessage()) {
                case "403" -> ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                case "404" -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                default -> ResponseEntity.badRequest().build();
            };
        }
    }
}
