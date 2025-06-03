package src.view;

import src.model.Game;
import src.model.Player;
import src.model.utils.BoardType;
import src.utils.IAction;

public interface View {
    void displayGameState(Game game);
    BoardType getBoardType();
    int getNumberOfPlayers();
    IAction readPlayerAction(Player player);
    void displayMessage(String message);
}
