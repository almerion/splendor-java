package src.view;

import src.model.Game;
import src.model.Player;
import src.model.utils.BoardType;
import src.utils.Action;
import src.utils.DropTokensAction;

public interface View {
    void displayGameState(Game game);
    BoardType getBoardType();
    int getNumberOfPlayers();
    Action readPlayerAction(Game game);
    void invalidActionMessage(Action action);
    void displayWin(Game game);
    DropTokensAction readDropTokensAction(Player currentPlayer);
    void invalidNumberOfPlayersMessage(int numberOfPlayers);
}
