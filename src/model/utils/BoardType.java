package src.model.utils;

public enum BoardType {
    SIMPLE, COMPLET;

    public static BoardType getBoardTypeFromString(String boardType) {
        return switch (boardType.toLowerCase()) {
            case "simple" -> BoardType.SIMPLE;
            case "complet" -> BoardType.COMPLET;
            default -> throw new IllegalArgumentException();
        };
    }

    public static boolean isValidBoardType(String boardType) {
        return switch (boardType.toLowerCase()) {
            case "simple", "complet" -> true;
            default -> false;
        };
    }
}
