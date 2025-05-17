package src.model.utils;

public enum Gem {
    GREEN, WHITE, BLUE, BLACK, RED, YELLOW;

    public static Gem getGemFromColor(String color) {
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
}
