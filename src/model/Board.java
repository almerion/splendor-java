package src.model;

import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Board {
    private static final Path CARDS_PATH = Paths.get("src", "ressources", "cards.csv");
    private static final Path NOBLES_PATH = Paths.get("src", "ressources", "nobles.csv");
    private final EnumMap<Level, List<Card>> cardRows;
    private final List<Noble> nobles;
    private GemBank gemBank;
    private final BoardType boardType;
    private final int cardsShown;
    private final int noblesShown;

    public Board(EnumMap<Level, List<Card>> cardRows, List<Noble> nobles, GemBank gemBank, BoardType boardType, int cardsShown, int noblesShown) {
        Objects.requireNonNull(cardRows);
        Objects.requireNonNull(nobles);
        Objects.requireNonNull(gemBank);
        Objects.requireNonNull(boardType);
        this.cardRows = cardRows;
        this.nobles = nobles;
        this.gemBank = gemBank;
        this.boardType = boardType;
        if (cardsShown < 0) {
            throw new IllegalArgumentException("Invalid number of cards shown: " + cardsShown);
        }
        this.cardsShown = cardsShown;
        if (noblesShown < 0) {
            throw new IllegalArgumentException("Invalid number of nobles shown: " + noblesShown);
        }
        this.noblesShown = noblesShown;
    }

    public BoardType boardType() {
        return boardType;
    }

    public int cardsShown() {
        return cardsShown;
    }

    public int noblesShown() {
        return noblesShown;
    }

    public Map<Level, List<Card>> cardRows() {
        var copy = new EnumMap<Level, List<Card>>(Level.class);
        for (var entry : cardRows.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().stream().limit(cardsShown).toList());
        }

        return Map.copyOf(copy);
    }

    public Card getCard(Level level, int index) {
        Objects.requireNonNull(level);
        if (index < 0 || index > cardsShown || index >= cardRows.get(level).size()) {
            return null;
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
        return nobles.stream().limit(noblesShown).toList();
    }

    public GemBank gemBank() {
        return gemBank;
    }

    private static EnumMap<Level, List<Card>> initializeSimpleCardRows() {
        var cards = new ArrayList<Card>();
        for (var i = 0; i < 8; i++) {
            for (var gem : Gem.values()) {
                if (gem == Gem.YELLOW) {
                    continue; // Skip yellow gem for simple board
                }
                var price = new GemBank(Map.of(gem, 3));
                cards.add(new Card(
                        gem,
                        price,
                        1,
                        Level.ONE
                ));
            }
        }
        Collections.shuffle(cards);
        return new EnumMap<>(Map.of(
                Level.ONE, cards,
                Level.TWO, List.of(),
                Level.THREE, List.of()
        ));
    }

    public void addGems(GemBank gems) {
        Objects.requireNonNull(gems);
        this.gemBank = gemBank.add(gems);
    }

    public void removeGems(GemBank gems) {
        Objects.requireNonNull(gems);
        if (!gemBank.canSubtractBy(gems, false)) {
            throw new IllegalArgumentException("Not enough gems in the bank to remove");
        }
        this.gemBank = gemBank.subtract(gems);
    }

    public static Board createBoard(int numberPlayers, BoardType boardType) {
        Objects.requireNonNull(boardType);
        if (numberPlayers < 2 || numberPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4");
        }

        List<Noble> nobles;
        EnumMap<Level, List<Card>> cardRows;
        GemBank gemBank;
        int cardsShown, noblesShown;
        switch (boardType) {
            case SIMPLE:
                nobles = List.of();
                cardRows = initializeSimpleCardRows();
                break;
            case COMPLET:
                if (!Files.isRegularFile(CARDS_PATH)) {
                    throw new IllegalStateException(CARDS_PATH + " file not found");
                }
                if (!Files.isRegularFile(NOBLES_PATH)) {
                    throw new IllegalStateException(NOBLES_PATH + " file not found");
                }
                nobles = NobleCsvReader.loadNobles(NOBLES_PATH);
                cardRows = CardCsvReader.loadCards(CARDS_PATH);
                break;
            default:
                throw new IllegalArgumentException();
        }
        gemBank = GemBank.bankFromPlayers(numberPlayers);
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
        return new Board(cardRows, nobles, gemBank, boardType, cardsShown, noblesShown);
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
        nobles.stream().limit(noblesShown).forEach(noble -> sb.append(noble.toString()).append(", \n"));
        sb.append("État de la banque : \n").append(gemBank.toString()).append("\n");
        return sb.toString();
    }
}
