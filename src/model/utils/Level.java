package src.model.utils;

public enum Level {
    ONE,TWO,THREE;

    public static Level getLevelFromInt(int level) {
        return switch (level) {
            case 1 -> Level.ONE;
            case 2 -> Level.TWO;
            case 3 -> Level.THREE;
            default -> throw new IllegalArgumentException();
        };
    }
}
