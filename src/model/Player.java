package src.model;

import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.Gem;

import java.util.*;

public class Player {

    private final ArrayList<Card> cards = new ArrayList<>();
    private final ArrayList<Noble> nobles = new ArrayList<>();
    private GemBank gems = new GemBank(new EnumMap<>(Gem.class));
    private final ArrayList<Card> reservedCards = new ArrayList<>();

    public List<Card> reservedCards() {
        return List.copyOf(reservedCards);
    }

    public List<Card> cards() {
        return List.copyOf(cards);
    }

    public GemBank gems() {
        return gems;
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card);
        cards.add(card);
    }

    public GemBank getBonuses() {
        var map = new HashMap<Gem, Integer>();
        cards.forEach(c -> map.merge(c.bonus(), 1, Integer::sum));
        return new GemBank(map);
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

        var cardPriceWithBonuses = card.price().subtractOrZero(getBonuses());
        return this.gems.canSubtractBy(cardPriceWithBonuses, true);
    }


    public void reserveCard(Card card) {
        Objects.requireNonNull(card);
        if (canReserveCard(card)) {
            reservedCards.add(card);
        } else {
            throw new IllegalArgumentException("Cannot reserve card");
        }
    }

    public void addGems(GemBank gemsToAdd) {
        Objects.requireNonNull(gemsToAdd);
        this.gems = this.gems.add(gemsToAdd);
    }

    // this function will be used to remove gems if the player has too much of them, we don't compensate with yellow gems
    public void removeGems(GemBank gemsToRemove) {
        Objects.requireNonNull(gemsToRemove);
        if (!gems.canSubtractBy(gemsToRemove, false)) {
            throw new IllegalArgumentException("Nombre de gemmes insuffisant pour retirer");
        }
        this.gems = this.gems.subtract(gemsToRemove);
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
        sb.append(gems.toString()).append("\n");
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
