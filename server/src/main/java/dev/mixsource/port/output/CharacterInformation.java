package dev.mixsource.port.output;

import dev.mixsource.model.entity.CharacterEntity;

import java.util.Set;

public interface CharacterInformation {
    Set<CharacterEntity> retrieveUserCharacters(final long userId);
}
