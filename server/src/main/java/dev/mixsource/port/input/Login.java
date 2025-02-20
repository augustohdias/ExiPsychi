package dev.mixsource.port.input;

import dev.mixsource.model.request.LoginRequest;
import dev.mixsource.model.response.LoginResponse;
import org.springframework.http.ResponseEntity;


public interface Login {
    ResponseEntity<LoginResponse> login(final LoginRequest loginRequest);
}
