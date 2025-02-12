package dev.mixsource.adapter.components;

import dev.mixsource.model.Action;
import dev.mixsource.model.Character;
import dev.mixsource.model.entity.UserEntity;
import dev.mixsource.model.request.LoginRequest;
import dev.mixsource.model.response.LoginResponse;
import dev.mixsource.port.input.LoginFacade;
import dev.mixsource.port.input.UserAuthentication;
import dev.mixsource.port.output.CharacterInformation;
import dev.mixsource.port.output.UserInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginFacadeService implements LoginFacade {
    @Autowired
    private final UserAuthentication authenticationService;

    @Autowired
    private final UserInformation userInformationService;

    @Autowired
    private final CharacterInformation characterInformationService;

    @Override
    public LoginResponse performLogin(LoginRequest request) {
        final Optional<String> maybeToken = authenticationService.authenticateUser(request.getUsername(), request.getPassword());
        if (maybeToken.isEmpty()) {
            throw new RuntimeException("403");
        }
        final String token = maybeToken.get();

        final Optional<UserEntity> maybeUser = userInformationService.retrieveUserByUsername(request.getUsername());
        if (maybeUser.isEmpty()) {
            throw new RuntimeException("404");
        }
        final UserEntity userEntity = maybeUser.get();

        final Set<Character> characters = characterInformationService
                .retrieveUserCharacters(userEntity.getId())
                .stream()
                .map(c -> new Character(c.getId(), c.getX(), c.getY(), Action.IDLE, c.getName()))
                .collect(Collectors.toSet());
        return new LoginResponse(characters, token);
    }
}
