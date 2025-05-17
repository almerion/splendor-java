package src.utils;

import src.model.utils.Gem;
import src.model.utils.Level;

import java.util.Map;
import java.util.Objects;

public record Action(ActionType type, Map<Gem, Integer> gems, int cardIndex, Level cardLevel) {
    public Action {
        Objects.requireNonNull(type);
        if (gems == null && (cardIndex < 0 || cardIndex > 3 || cardLevel == null)) {
            throw new IllegalArgumentException();
        }
        if (cardIndex == -1) {
            Objects.requireNonNull(gems);
        }
    }

    public Action(ActionType type, Map<Gem, Integer> gems) {
        this(type, gems, -1, null);
    }

    public Action(ActionType type, int cardIndex, Level cardLevel) {
        this(type, null, cardIndex, cardLevel);
    }
    public Action(ActionType type, int cardIndex) {
        this(type, null, cardIndex, null);
    }
}
