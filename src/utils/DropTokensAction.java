package src.utils;

import src.model.GemBank;

import java.util.Objects;

public record DropTokensAction(GemBank gemBank) implements IAction {
    public DropTokensAction {
        Objects.requireNonNull(gemBank);
    }
}
