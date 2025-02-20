package dev.mixsource.adapter.components;

import dev.mixsource.model.entity.UserEntity;
import dev.mixsource.port.output.UserInformation;
import dev.mixsource.port.output.repository.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInformationService implements UserInformation {
    @Autowired
    private final Users users;

    @Override
    public Optional<UserEntity> retrieveUserByUsername(String username) {
        return users.findByUsername(username);
    }
}
