package src.model;

import src.model.utils.Gem;

import java.util.*;

public class GemBank {
    private final EnumMap<Gem, Integer> bank;

    public GemBank(int nbPlayers) {
        this.bank = switch (nbPlayers) {
            case 2 -> initMap(4);
            case 3 -> initMap(5);
            case 4 -> initMap(7);
            default -> throw new IllegalArgumentException();
        };
    }

    private static EnumMap<Gem, Integer> initMap(int nbOtherTokens) {
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

    public Map<Gem, Integer> bank() {
        return Map.copyOf(bank);
    }

    public int get(Gem type) {
        return bank.getOrDefault(type, 0);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        bank.forEach((gem, number) -> sb.append(gem.name()).append(": ").append(number).append("\n"));
        return sb.toString();
    }
}
