package src.model;

import src.model.utils.Gem;

import java.util.*;

public class GemBank {
    private final Map<Gem, Integer> bank;

    public GemBank(Map<Gem, Integer> bank) {
        Objects.requireNonNull(bank);

        if (bank.values().stream().anyMatch(val -> val < 0)) {
            throw new IllegalArgumentException("Invalid number of gems");
        }

        this.bank = Map.copyOf(bank);
    }

    public GemBank(int black, int red, int yellow, int white, int green, int blue ) {
        if (black < 0 || red < 0 || yellow < 0 || white < 0 || green < 0 || blue < 0) {
            throw new IllegalArgumentException("Invalid number of gems");
        }

        this(Map.of(
                Gem.BLACK, black,
                Gem.RED, red,
                Gem.YELLOW, yellow,
                Gem.WHITE, white,
                Gem.GREEN, green,
                Gem.BLUE, blue
        ));
    }

    public Map<Gem, Integer> getBank() {
        return Map.copyOf(bank);
    }

    public GemBank add(GemBank bank) {
        Objects.requireNonNull(bank);

        var newMap = new EnumMap<Gem, Integer>(Gem.class);
        for (var gem : Gem.values()) {
            newMap.put(gem, bank.get(gem) + this.get(gem));
        }

        return new GemBank(Map.copyOf(newMap));
    }

    public GemBank subtract(GemBank bank) {
        Objects.requireNonNull(bank);
        if (!this.canSubtractBy(bank, true)) {
            throw new IllegalArgumentException("Nombre de gemmes insuffisantes pour soustraire");
        }
        return subtractInternal(bank, true);
    }

    public GemBank subtractOrZero(GemBank bank) {
        Objects.requireNonNull(bank);
        return subtractInternal(bank, false);
    }

    private GemBank subtractInternal(GemBank bank, boolean compensateWithYellow) {
        var newMap = new EnumMap<Gem, Integer>(Gem.class);
        int requiredYellowGems = 0;

        for (var gem : Gem.values()) {
            int toSubtract = bank.get(gem);
            int currentGems = this.get(gem);
            int remaining = currentGems - toSubtract;

            if (compensateWithYellow && remaining < 0) {
                requiredYellowGems += -remaining;
                remaining = 0;
            }

            newMap.put(gem, Math.max(remaining, 0));
        }

        if (compensateWithYellow && requiredYellowGems > 0) {
            newMap.merge(Gem.YELLOW, -requiredYellowGems, Integer::sum);
        }

        return new GemBank(Map.copyOf(newMap));
    }

    public boolean canSubtractBy(GemBank bank, boolean compensateWithYellow) {
        Objects.requireNonNull(bank);
        var requiredYellowGems = 0;

        for (var gem : Gem.values()) {
            var result = this.get(gem) - bank.get(gem);
            if (result < 0 && compensateWithYellow) {
                requiredYellowGems += bank.get(gem) - this.get(gem);
            } else if (result < 0) {
                return false;
            }
        }

        return this.get(Gem.YELLOW) >= requiredYellowGems;
    }

    public int get(Gem type) {
        return bank.getOrDefault(type, 0);
    }

    public static GemBank bankFromPlayers(int numberOfPlayers) {
        var nbDefaultGems =  switch (numberOfPlayers) {
            case 2 -> 4;
            case 3 -> 5;
            case 4 -> 7;
            default -> throw new IllegalArgumentException();
        };

        EnumMap<Gem, Integer> gemBank = new EnumMap<>(Gem.class);
        gemBank.put(Gem.RED,    nbDefaultGems);
        gemBank.put(Gem.BLUE,   nbDefaultGems);
        gemBank.put(Gem.BLACK,  nbDefaultGems);
        gemBank.put(Gem.GREEN,  nbDefaultGems);
        gemBank.put(Gem.WHITE,  nbDefaultGems);
        gemBank.put(Gem.YELLOW, 5);

        return new GemBank(Map.copyOf(gemBank));
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        bank.forEach((gem, number) -> sb.append(gem.name()).append(": ").append(number).append("\n"));
        return sb.toString();
    }
}
