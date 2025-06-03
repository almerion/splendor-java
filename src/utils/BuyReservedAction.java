package src.utils;

public record BuyReservedAction(int reservedCardIndex) implements IAction {
    public BuyReservedAction {
        if (reservedCardIndex < 0) {
            throw new IllegalArgumentException("Index de carte réservée invalide");
        }
    }
}
