package src.model;

import src.model.utils.Gem;

import java.util.*;

public record GemBank(Map<Gem, Integer> bank) {
    public GemBank {
        Objects.requireNonNull(bank);
    }

    public GemBank(int nbPlayers) {
        int nbOtherTokens = switch (nbPlayers) {
            case 2 -> 4;
            case 3 -> 5;
            case 4 -> 7;
            default -> throw new IllegalArgumentException();
        };

        this(Map.of(
                Gem.RED, nbOtherTokens,
                Gem.BLUE, nbOtherTokens,
                Gem.BLACK, nbOtherTokens,
                Gem.GREEN, nbOtherTokens,
                Gem.WHITE, nbOtherTokens,
                Gem.YELLOW, 5
        ));
    }
}
