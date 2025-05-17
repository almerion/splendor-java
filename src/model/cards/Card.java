package src.model.cards;

import src.model.utils.Gem;
import src.model.utils.Level;

import java.util.Map;
import java.util.Objects;

public record Card(Gem bonus, Map<Gem, Integer> price, int prestigePoints, Level level) {
    public Card {
        Objects.requireNonNull(bonus);
        Objects.requireNonNull(price);
        if (prestigePoints < 0) {
            throw new IllegalArgumentException();
        }
    }
}
