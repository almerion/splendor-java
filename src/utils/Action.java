package src.utils;

public sealed interface Action permits BuyReservedAction, BuyCardAction, DropTokensAction, ReserveCardAction, TakeGemsAction {
}
