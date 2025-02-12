package dev.mixsource.port.input;

import java.util.Optional;

public interface UserAuthentication {
    Optional<String> authenticateUser(final String username, final String password);
}
