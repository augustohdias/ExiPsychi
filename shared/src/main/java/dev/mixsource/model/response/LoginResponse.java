package dev.mixsource.model.response;

import java.util.Set;
import dev.mixsource.model.CharacterModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Set<CharacterModel> characters;
    private String userToken;
} 