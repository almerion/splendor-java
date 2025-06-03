package src.utils;

import src.model.utils.Level;

import java.util.Objects;

public record BuyCardAction(Level cardLevel, int cardIndex) implements IAction {
    public BuyCardAction {
        Objects.requireNonNull(cardLevel);
        if (cardIndex < 0) {
            throw new IllegalArgumentException("Index de carte invalide");
        }
    }
}
