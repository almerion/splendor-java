package src.model.utils;

import src.model.cards.Card;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CardCsvReader {
    private static final int LEVEL_COL = 0;
    private static final int GEM_COLOR_COL = 1;
    private static final int PRESTIGE_COL = 2;
    private static final int ILLUSTRATION_COL = 4;
    private static final int WHITE_COL = 5;
    private static final int BLUE_COL = 6;
    private static final int GREEN_COL = 7;
    private static final int RED_COL = 8;
    private static final int BLACK_COL = 9;

    public static EnumMap<Level, List<Card>> loadCards(Path filePath) {
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException();
        }
        Map<Level, List<Card>> result = new EnumMap<>(Level.class);
        for (Level lvl : Level.values()) {
            result.put(lvl, new ArrayList<>());
        }

        try (var reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            // skip useless line
            reader.readLine();
            reader.readLine();

            Level currentLevel = null;
            Gem currentGem = null;
            String currentIllustration = null;
            String line;

            while ((line = reader.readLine()) != null) {
                int prestigePoints;
                if (line.isBlank()) continue;
                String[] cols = line.split(",", -1);

                if (!cols[CardCsvReader.LEVEL_COL].isBlank()) {
                    currentLevel = Level.getLevelFromInt(Integer.parseInt(cols[CardCsvReader.LEVEL_COL]));
                }
                if (!cols[CardCsvReader.GEM_COLOR_COL].isBlank()) {
                    currentGem = Gem.getGemFromColor(cols[CardCsvReader.GEM_COLOR_COL]);
                }
                if (!cols[CardCsvReader.ILLUSTRATION_COL].isBlank()) {
                    // replace because of "Gioielleria" ?
                    currentIllustration = cols[CardCsvReader.ILLUSTRATION_COL].replaceAll("\"", "");
                }
                prestigePoints = parseOrZero(cols[CardCsvReader.PRESTIGE_COL]);

                if(currentLevel == null || currentGem == null || currentIllustration == null) {
                    throw new IllegalStateException();
                }

                var price = getPrice(cols);
                Card card = new Card(
                        currentGem,
                        Map.copyOf(price),
                        prestigePoints,
                        currentLevel
                );
                result.get(currentLevel).add(card);
            }
        } catch (IOException e) {
            throw new IllegalStateException();
        }

        for (List<Card> cards : result.values()) {
            Collections.shuffle(cards);
        }

        return new EnumMap<>(result);
    }

    private static Map<Gem, Integer> getPrice(String[] cols) {
        Map<Gem, Integer> price = new EnumMap<>(Gem.class);
        price.put(Gem.WHITE, parseOrZero(cols[WHITE_COL]));
        price.put(Gem.BLUE, parseOrZero(cols[BLUE_COL]));
        price.put(Gem.GREEN, parseOrZero(cols[GREEN_COL]));
        price.put(Gem.RED, parseOrZero(cols[RED_COL]));
        price.put(Gem.BLACK, parseOrZero(cols[BLACK_COL]));

        return price;
    }

    private static int parseOrZero(String s) {
        if (s == null || s.isBlank()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
