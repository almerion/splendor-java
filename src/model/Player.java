package src.model;

import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.Gem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {

    private final ArrayList<Card> cards = new ArrayList<>();
    private final ArrayList<Noble> nobles = new ArrayList<>();
    private final Map<Gem, Integer> gems = new HashMap<>();
    private final ArrayList<Card> reservedCards = new ArrayList<>();

    // Accessors
    public List<Card> getReservedCards() {
        return reservedCards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Map<Gem, Integer> getGems() {
        return gems;
    }

    public Map<Gem, Integer> getBonuses() {
        var map = new HashMap<Gem, Integer>();
        cards.forEach(c -> map.merge(c.bonus(), 1, Integer::sum));
        return map;
    }

    public List<Noble> getNobles() {
        return nobles;
    }

    public int getPoints() {
        return cards.stream().map(c -> c.prestigePoints()).reduce(0, Integer::sum)
                + nobles.stream().map(n -> n.prestigePoints()).reduce(0, Integer::sum);
    }


    // Capability checks
    public boolean canReserveCard(Card card) {
        return reservedCards.size() < 3;
    }

    public boolean canPurchaseCard(Card card) {
        var bool = true;
        for(var c : card.price().entrySet()) {
            if (gems.get(c.getKey()) < c.getValue())
                bool = false;
        }
        return bool;
    }

    public boolean canClaimNoble(Noble noble) {
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
        reservedCards.add(card);
    }

    public void purchaseCard(Card card, GemBank bank) {
        cards.add(card);
        //TO DO add gems to the bank
    }

    public void takeGems(Map<Gem, Integer> gemsToTake, GemBank bank) {
        for (var g : gemsToTake.entrySet()) {
            gems.merge(g.getKey(), g.getValue(),Integer::sum);
            //TO DO remove gems from the bank
        }
    }

    public void claimNoble(Noble noble) {
        nobles.add(noble);
    }


}
