package dev.mixsource.adapter.components;

import dev.mixsource.model.Character;
import dev.mixsource.port.input.ActionEvaluator;
import dev.mixsource.port.output.repository.Characters;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterActionEvaluatorFacade implements ActionEvaluator<Character> {
    @Autowired
    private final Characters characters;

    @Override
    public boolean isValidAction(final Character entity) {
        return switch (entity.getAction()) {
            case MOVE, SIT, ATTACK -> true; // TODO: Implement checking
            default -> false;
        };
    }

    @Override
    public <U> boolean isValidInteraction(final Character entity, final U object) {
        return false;
    }

    private boolean isMoveAllowed(final Character entity) {
        characters.findById(entity.getId());
        return false;
    }
}
