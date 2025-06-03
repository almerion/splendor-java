package src.model.cards;

import src.model.GemBank;
import src.model.utils.Gem;

import java.util.Objects;

// We consider the bonuses as a gemBank so the required bonuses can also be represented as a GemBank.
public record Noble(GemBank requiredBonuses, int prestigePoints) {
    public Noble {
        Objects.requireNonNull(requiredBonuses);
        if (requiredBonuses.get(Gem.YELLOW) != 0) {
            throw new IllegalArgumentException("Les nobles ne peuvent pas avoir de bonus jaune");
        }
        if (prestigePoints < 1) {
            throw new IllegalArgumentException("Les nobles doivent avoir au moins 1 point de prestige");
        }
    }
}
