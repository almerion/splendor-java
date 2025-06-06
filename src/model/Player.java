package src.model;

import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.Gem;

import java.util.*;

public class Player {
    private static final int MAX_RESERVED_CARDS = 3;
    private final ArrayList<Card> cards;
    private final ArrayList<Noble> nobles;
    private GemBank gems;
    private final ArrayList<Card> reservedCards;

    public Player(ArrayList<Card> cards, ArrayList<Noble> nobles, GemBank gems, ArrayList<Card> reservedCards) {
        Objects.requireNonNull(cards);
        Objects.requireNonNull(nobles);
        Objects.requireNonNull(gems);
        Objects.requireNonNull(reservedCards);

        if (reservedCards.size() > MAX_RESERVED_CARDS) {
            throw new IllegalArgumentException("Too many reserved cards");
        }

        this.cards = new ArrayList<>(cards);
        this.nobles = new ArrayList<>(nobles);
        this.reservedCards = new ArrayList<>(reservedCards);
        this.gems = gems;
    }
    public static Player createEmptyPlayer() {
        return new Player(
                new ArrayList<>(),
                new ArrayList<>(),
                new GemBank(new EnumMap<>(Gem.class)),
                new ArrayList<>()
        );
    }

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
        return cards.stream().mapToInt(Card::prestigePoints).sum()
                + nobles.stream().mapToInt(Noble::prestigePoints).sum();
    }

    // Capability checks
    public boolean canReserveCard(Card card) {
        Objects.requireNonNull(card);
        return reservedCards.size() < MAX_RESERVED_CARDS;
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

    public boolean canClaimNoble(Noble noble) {
        Objects.requireNonNull(noble);
        return noble.requiredBonuses().canSubtractBy(getBonuses(), false);
    }

    public void claimNoble(Noble noble) {
        Objects.requireNonNull(noble);
        if (!canClaimNoble(noble)) {
            throw new IllegalArgumentException("Pas assez de bonus pour réclamer ce noble");
        }

        nobles.add(noble);
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
