package src.utils;

import src.model.GemBank;

import java.util.Objects;

public record TakeGemsAction(GemBank gemBank) implements IAction {
    public TakeGemsAction {
        Objects.requireNonNull(gemBank);
    }
}
