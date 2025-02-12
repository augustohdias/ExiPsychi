package dev.mixsource.model.response;

import dev.mixsource.model.Character;

import java.util.Set;

public record LoginResponse(Set<Character> characters, String userToken) {
}
