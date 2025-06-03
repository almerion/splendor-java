package src.model.cards;

import src.model.GemBank;
import src.model.utils.Gem;
import src.model.utils.Level;

import java.util.Objects;

public record Card(Gem bonus, GemBank price, int prestigePoints, Level level) {
    public Card {
        Objects.requireNonNull(bonus);
        Objects.requireNonNull(price);
        if (prestigePoints < 0) {
            throw new IllegalArgumentException();
        }
    }

}
