package src.model.cards;

import src.model.utils.Gem;

import java.util.Map;
import java.util.Objects;

public record Noble(Map<Gem, Integer> requiredBonuses, int prestigePoints) {
    public Noble {
        Objects.requireNonNull(requiredBonuses);
        if (prestigePoints < 1) {
            throw new IllegalArgumentException();
        }
    }
}
