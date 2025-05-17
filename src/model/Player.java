package src.model;

import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.Gem;

import java.util.*;

public class Player {

    private final ArrayList<Card> cards = new ArrayList<>();
    private final ArrayList<Noble> nobles = new ArrayList<>();
    private final Map<Gem, Integer> gems = new HashMap<>();
    private final ArrayList<Card> reservedCards = new ArrayList<>();

    public List<Card> reservedCards() {
        return List.copyOf(reservedCards);
    }

    public List<Card> cards() {
        return List.copyOf(cards);
    }

    public Map<Gem, Integer> gems() {
        return Map.copyOf(gems);
    }

    public Map<Gem, Integer> getBonuses() {
        var map = new HashMap<Gem, Integer>();
        cards.forEach(c -> map.merge(c.bonus(), 1, Integer::sum));
        return Map.copyOf(map);
    }

    public List<Noble> nobles() {
        return List.copyOf(nobles);
    }

    public int getPoints() {
        return cards.stream().map(Card::prestigePoints).reduce(0, Integer::sum)
                + nobles.stream().map(Noble::prestigePoints).reduce(0, Integer::sum);
    }


    // Capability checks
    public boolean canReserveCard(Card card) {
        Objects.requireNonNull(card);
        return reservedCards.size() < 3;
    }

    public boolean canPurchaseCard(Card card) {
        Objects.requireNonNull(card);

        int jokers = gems.getOrDefault(Gem.YELLOW, 0);
        var bonuses = getBonuses();
        int neededJokers = 0;

        for (var entry : card.price().entrySet()) {
            Gem color = entry.getKey();
            int cost = entry.getValue();
            int discount = bonuses.getOrDefault(color, 0);
            int toPay = cost - discount;
            if (toPay > 0) {
                int have = gems.getOrDefault(color, 0);
                if (have < toPay) {
                    neededJokers += (toPay - have);
                }
            }
        }

        return neededJokers <= jokers;
    }

    public boolean canClaimNoble(Noble noble) {
        Objects.requireNonNull(noble);
        var bool = true;
        var bonuses = this.getBonuses();
        for(var c : noble.requiredBonuses().entrySet()) {
            if (bonuses.get(c.getKey()) < c.getValue())
                bool = false;
        }
        return bool;
    }


    // Actions
    public void reserveCard(Card card) {
        Objects.requireNonNull(card);
        if (canReserveCard(card)) {
            reservedCards.add(card);
        } else {
            throw new IllegalArgumentException("Cannot reserve card");
        }
    }

    public void purchaseCard(Card card, GemBank bank) {
        Objects.requireNonNull(card);
        Objects.requireNonNull(bank);
        if (!canPurchaseCard(card)) {
            throw new IllegalArgumentException("La carte " + card.toString() + " ne peut pas être achetée");
        }

        var bonuses = getBonuses();
        cards.add(card);

        for (var entry : card.price().entrySet()) {
            var color = entry.getKey();
            var cost = entry.getValue();
            var discount = bonuses.getOrDefault(color, 0);
            var toPay = Math.max(0, cost - discount);

            var fromColor = Math.min(gems.getOrDefault(color, 0), toPay);
            if (fromColor > 0) {
                gems.put(color, gems.get(color) - fromColor);
                bank.add(color, fromColor);
            }

            var remaining = toPay - fromColor;
            if (remaining > 0) {
                var haveJokers = gems.getOrDefault(Gem.YELLOW, 0);
                var useJokers = Math.min(haveJokers, remaining);
                gems.put(Gem.YELLOW, haveJokers - useJokers);
                bank.add(Gem.YELLOW, useJokers);
            }
        }
    }

    public static boolean canTakeGems(Map<Gem,Integer> gemsToTake, GemBank bank) {
        Objects.requireNonNull(gemsToTake);
        Objects.requireNonNull(bank);
        if (gemsToTake.getOrDefault(Gem.YELLOW, 0) > 0) {
            return false;
        }
        int total = gemsToTake.values().stream().mapToInt(Integer::intValue).sum();

        if (total == 3) {
            if (gemsToTake.size() != 3) return false;
            if (gemsToTake.values().stream().anyMatch(v -> v != 1)) return false;
            return gemsToTake.entrySet().stream()
                    .allMatch(e -> bank.get(e.getKey()) >= 1);
        }

        else if (total == 2) {
            if (gemsToTake.size() != 1) return false;
            var gem = gemsToTake.keySet().iterator().next();
            return bank.get(gem) >= 4;
        }

        return false;
    }

    public void takeGems(Map<Gem, Integer> gemsToTake, GemBank bank) {
        Objects.requireNonNull(gemsToTake);
        Objects.requireNonNull(bank);

        if (!canTakeGems(gemsToTake, bank)) {
            throw new IllegalArgumentException("Nombre de gemmes invalide");
        }

        for (var g : gemsToTake.entrySet()) {
            gems.merge(g.getKey(), g.getValue(),Integer::sum);
            bank.remove(g.getKey(), g.getValue());
        }
    }

    public void claimNoble(Noble noble) {
        Objects.requireNonNull(noble);
        if (canClaimNoble(noble)) {
            nobles.add(noble);
        } else  {
            throw new IllegalArgumentException();
        }
    }

    public void removeReservedCard(int index) {
        if (index < 0 || index >= reservedCards.size()) {
            throw new IllegalArgumentException();
        }
        reservedCards.remove(index);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Gems: ");
        gems.forEach((gem, number) -> sb.append(gem.name()).append(": ").append(number).append("\n"));
        sb.append("Reserved cards: ");
        reservedCards.forEach(card -> sb.append(card.toString()).append(", \n"));
        sb.append("Cards: ");
        cards.forEach(card -> sb.append(card.toString()).append(", \n"));
        sb.append("Nobles: ");
        nobles.forEach(noble -> sb.append(noble.toString()).append(", \n"));
        sb.append("Points de prestige: ").append(getPoints());
        return sb.toString();
    }
}
