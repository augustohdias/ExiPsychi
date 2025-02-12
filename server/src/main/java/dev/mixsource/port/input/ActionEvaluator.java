package dev.mixsource.port.input;

import dev.mixsource.model.Character;

public interface ActionEvaluator<T> {
    boolean isValidAction(final T entity);

    <U> boolean isValidInteraction(final T entity, final U object);
}
