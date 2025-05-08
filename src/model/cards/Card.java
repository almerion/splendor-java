package src.model.cards;

import src.model.utils.Gem;
import src.model.utils.Level;

import java.util.Map;
import java.util.Objects;

public record Card(Gem bonus, Map<Gem, Integer> price, int presigePoints, Level level) {
    public Card {
        Objects.requireNonNull(bonus);
        Objects.requireNonNull(price);
        if (presigePoints < 0) {
            throw new IllegalArgumentException();
        }
    }
}
