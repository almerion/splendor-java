package src.model.utils;

public enum BoardType {
    SIMPLE, COMPLETE;

    public static BoardType getBoardTypeFromString(String boardType) {
        return switch (boardType.toLowerCase()) {
            case "simple" -> BoardType.SIMPLE;
            case "complet" -> BoardType.COMPLETE;
            default -> throw new IllegalArgumentException();
        };
    }
}
