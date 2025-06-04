package src.model.utils;

import src.model.GemBank;
import src.model.cards.Noble;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class NobleCsvReader {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final int PRESTIGE_COL = 0;
    private static final int NAME_COL = 1;
    private static final int GREEN_COL = 2;
    private static final int BLUE_COL = 3;
    private static final int RED_COL = 4;
    private static final int WHITE_COL = 5;
    private static final int BLACK_COL = 6;

    public static List<Noble> loadNobles(Path filePath) {
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("Path does not point to a regular file: " + filePath);
        }

        List<Noble> nobles = new ArrayList<>();

        try (var reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            // skip useless line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] cols = line.split(",", -1);

                int prestige = parseOrZero(cols[PRESTIGE_COL]);

                String name = cols[NAME_COL].trim();

                Map<Gem, Integer> requirements = new EnumMap<>(Gem.class);
                requirements.put(Gem.GREEN, parseOrZero(cols[GREEN_COL]));
                requirements.put(Gem.BLUE, parseOrZero(cols[BLUE_COL]));
                requirements.put(Gem.RED, parseOrZero(cols[RED_COL]));
                requirements.put(Gem.WHITE, parseOrZero(cols[WHITE_COL]));
                requirements.put(Gem.BLACK, parseOrZero(cols[BLACK_COL]));

                GemBank costBank = new GemBank(requirements);

                Noble noble = new Noble(costBank, prestige, name);
                nobles.add(noble);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read CSV file: " + filePath, e);
        }

        Collections.shuffle(nobles);
        return nobles;
    }

    private static int parseOrZero(String s) {
        if (s == null || s.isBlank() || !NUMBER_PATTERN.matcher(s).matches())
            return 0;
        return Integer.parseInt(s);
    }
}
