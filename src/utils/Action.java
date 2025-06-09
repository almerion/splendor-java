package src.utils;

import src.model.GemBank;
import src.model.utils.Gem;

import java.util.Map;

public sealed interface Action permits BuyReservedAction, BuyCardAction, DropTokensAction, ReserveCardAction, TakeGemsAction {
    static Action invalidAction() {
        return new TakeGemsAction(new GemBank(Map.of(Gem.YELLOW, 1000)));
    }
}
