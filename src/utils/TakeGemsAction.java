package src.utils;

import src.model.GemBank;

import java.util.Objects;

public record TakeGemsAction(GemBank gemBank) implements Action {
    public TakeGemsAction {
        Objects.requireNonNull(gemBank);
    }
}
