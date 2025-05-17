package src.model;

import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.BoardType;
import src.model.utils.CardCsvReader;
import src.model.utils.Gem;
import src.model.utils.Level;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Board {
    private final Path cardsPath = Paths.get("src", "ressources", "cards.csv");
    private final EnumMap<Level, List<Card>> cardRows;
    private final List<Noble> nobles;
    private final GemBank gemBank;
    private final int cardsShown;
    private final int noblesShown;

    public Board(int numberPlayers, BoardType boardType)  {
        switch (boardType) {
            case SIMPLE:
                nobles = List.of();
                cardRows = initializeSimpleCardRows();
                break;
            case COMPLETE:
                if (!Files.isRegularFile(cardsPath)) {
                    throw new IllegalStateException(cardsPath.toString() + " file not found");
                }
                nobles = List.of();
                cardRows = CardCsvReader.loadCards(cardsPath);
                break;
            default:
                throw new IllegalArgumentException();
        }
        this.gemBank = new GemBank(numberPlayers);
        switch (numberPlayers) {
            case 2 -> {
                cardsShown = 4;
                noblesShown = 3;
            }
            case 3 -> {
                cardsShown = 5;
                noblesShown = 4;
            }
            case 4 -> {
                cardsShown = 5;
                noblesShown = 5;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    public Map<Level, List<Card>> cardRows() {
        return Map.copyOf(cardRows);
    }

    public Card getCard(Level level, int index) {
        Objects.requireNonNull(level);
        if (index < 0 || index > cardsShown || index >= cardRows.get(level).size()) {
            throw new IllegalArgumentException();
        }

        return cardRows.get(level).get(index);
    }

    public void removeCard(Level level, int index) {
        Objects.requireNonNull(level);
        if (index < 0 || index > cardsShown || index >= cardRows.get(level).size()) {
            throw new IllegalArgumentException();
        }

        cardRows.get(level).remove(index);
    }

    public List<Noble> nobles() {
        return nobles;
    }

    public GemBank gemBank() {
        return gemBank;
    }

    public int cardsShown() {
        return cardsShown;
    }

    public int noblesShown() {
        return noblesShown;
    }

    private EnumMap<Level, List<Card>> initializeSimpleCardRows() {
        var cards = new ArrayList<Card>();

        for (Gem gem : Gem.values()) {
            var price = new EnumMap<Gem, Integer>(Gem.class);
            price.put(gem, 3);
            cards.add(new Card(
                    gem,
                    Map.copyOf(price),
                    1,
                    Level.ONE
            ));
        }
        Collections.shuffle(cards);
        return new EnumMap<>(Map.of(
                Level.ONE, cards,
                Level.TWO, List.of(),
                Level.THREE, List.of()
        ));
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Cartes par niveau:\n");
        cardRows.forEach((level, rows) -> {
            sb.append(level).append(" : \n");
            rows.stream().limit(cardsShown).forEach(card -> sb.append(card.toString()).append(", \n"));
        });
        sb.append("Nobles : \n");
        nobles.forEach(noble -> sb.append(noble.toString()).append(", \n"));
        sb.append("État de la banque : \n").append(gemBank.toString()).append("\n");
        return sb.toString();
    }
}
