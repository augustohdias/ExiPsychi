package dev.mixsource.port.input;

import dev.mixsource.model.request.LoginRequest;
import dev.mixsource.model.response.LoginResponse;

public interface LoginFacade {
    LoginResponse performLogin(final LoginRequest request);
}
