package src.model.utils;

public enum Gem {
    GREEN, WHITE, BLUE, BLACK, RED, YELLOW;

    public static Gem getGemFromColor(String color) {
        if (!Gem.isValidGem(color)) {
            throw new IllegalArgumentException("Invalid gem color: " + color);
        }
        return switch (color.toLowerCase()) {
            case "black" -> Gem.BLACK;
            case "red" -> Gem.RED;
            case "yellow" -> Gem.YELLOW;
            case "white" -> Gem.WHITE;
            case "green" -> Gem.GREEN;
            case "blue" -> Gem.BLUE;
            default -> throw new IllegalArgumentException();
        };
    }

    public static boolean isValidGem(String color) {
        switch (color.toLowerCase()) {
            case "black", "red", "yellow", "white", "green", "blue" -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
