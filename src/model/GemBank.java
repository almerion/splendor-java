package src.model;

import src.model.utils.Gem;

import java.util.*;

public record GemBank(Map<Gem, Integer> bank) {
    public GemBank {
        Objects.requireNonNull(bank);

        // copie car si on passe une map non mutable le add peut planter
        Map<Gem,Integer> copy = new EnumMap<>(Gem.class);
        copy.putAll(bank);
        bank = copy;
    }

    public GemBank(int nbPlayers) {
        this(switch (nbPlayers) {
            case 2 -> initMap(4);
            case 3 -> initMap(5);
            case 4 -> initMap(7);
            default -> throw new IllegalArgumentException();
        });
    }

    private static Map<Gem, Integer> initMap(int nbOtherTokens) {
        EnumMap<Gem, Integer> init = new EnumMap<>(Gem.class);
        init.put(Gem.RED,    nbOtherTokens);
        init.put(Gem.BLUE,   nbOtherTokens);
        init.put(Gem.BLACK,  nbOtherTokens);
        init.put(Gem.GREEN,  nbOtherTokens);
        init.put(Gem.WHITE,  nbOtherTokens);
        init.put(Gem.YELLOW, 5);
        return init;
    }

    public void add(Gem type, int number) {
        if (number < 0) throw new IllegalArgumentException();

        bank.merge(type, number, Integer::sum);
    }

    public void remove(Gem type, int number) {
        if (number < 0) throw new IllegalArgumentException();

        bank.compute(type, (gem, current) -> {
            if (current == null || current < number) {
                throw new IllegalArgumentException();
            }
            return current - number;
        });
    }

    public int get(Gem type) {
        return bank.getOrDefault(type, 0);
    }
}
