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
                nobles = initializeNobles();
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

    public static List<Noble> initializeNobles() {
        var noblesList = new ArrayList<Noble>(List.of(
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.GREEN,3,
                                Gem.BLUE,3,
                                Gem.RED,3
                        )),
                        3,
                        "Catherine de' Medici"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.BLACK,3,
                                Gem.BLUE,3,
                                Gem.WHITE,3
                        )),
                        3,
                        "Elisabeth Of Austria"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.BLACK,4,
                                Gem.WHITE,4
                        )),
                        3,
                        "Isabella I Of Castile"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.BLUE,4,
                                Gem.WHITE,4
                        )),
                        3,
                        "Niccolò Machiavelli"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.BLUE,4,
                                Gem.GREEN,4
                        )),
                        3,
                        "Suleiman The Magnificent"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.GREEN,3,
                                Gem.BLUE,3,
                                Gem.WHITE,3
                        )),
                        3,
                        "Anne Of Brittany"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.BLACK,3,
                                Gem.RED,3,
                                Gem.WHITE,3
                        )),
                        3,
                        "Charles V"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.BLACK,3,
                                Gem.RED,3,
                                Gem.GREEN,3
                        )),
                        3,
                        "Francis I Of France"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.BLACK,4,
                                Gem.RED,4
                        )),
                        3,
                        "Henry VII"
                ),
                new Noble(
                        new HashMap<Gem, Integer>(Map.of(
                                Gem.RED,4,
                                Gem.GREEN,4
                        )),
                        3,
                        "Mary Stuart"
                )
                ));
        Collections.shuffle(noblesList);
        return noblesList;
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
