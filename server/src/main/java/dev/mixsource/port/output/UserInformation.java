package dev.mixsource.port.output;

import dev.mixsource.model.entity.UserEntity;

import java.util.Optional;

public interface UserInformation {
    Optional<UserEntity> retrieveUserByUsername(final String username);
}
