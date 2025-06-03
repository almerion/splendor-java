package src.utils;

public sealed interface IAction permits BuyReservedAction, BuyCardAction, DropTokensAction, ReserveCardAction, TakeGemsAction {
}
