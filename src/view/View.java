package src.view;

import src.model.Game;
import src.model.Player;
import src.model.utils.BoardType;
import src.utils.Action;

public interface View {
    void displayGameState(Game game);
    BoardType getBoardType();
    int getNumberOfPlayers();
    Action readPlayerAction(Player player);
    void invalidActionMessage(Action action);
    void displayWin(Player winner);
}
