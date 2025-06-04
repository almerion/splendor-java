package src.utils;

import src.model.utils.Level;

import java.util.Objects;

public record ReserveCardAction(Level cardLevel, int cardIndex) implements Action {
    public ReserveCardAction {
        Objects.requireNonNull(cardLevel);
        if (cardIndex < 0) {
            throw new IllegalArgumentException("Index de carte invalide");
        }
    }
}
