package dev.mixsource.model.response;

import java.util.Set;
import dev.mixsource.model.Character;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Set<Character> characters;
    private String userToken;
} 