package dev.mixsource.adapter.components;

import dev.mixsource.model.entity.CharacterEntity;
import dev.mixsource.port.output.CharacterInformation;
import dev.mixsource.port.output.repository.Characters;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CharacterInformationService implements CharacterInformation {
    @Autowired
    private final Characters characters;

    @Override
    public Set<CharacterEntity> retrieveUserCharacters(long userId) {
        return new HashSet<>(characters.findAllByUserId(userId));
    }
}
