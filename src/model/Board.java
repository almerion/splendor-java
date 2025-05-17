package src.model;

import jdk.jshell.spi.ExecutionControl;
import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.BoardType;

import java.util.List;

public class Board {
    private final List<List<Card>> cardRows;
    private final List<Noble> nobles;
    private final GemBank gemBank;

    public Board(int numberPlayers, BoardType boardType) {
        cardRows = initializeCardsRow(boardType);
        this.nobles = initializeNobles(boardType);
        this.gemBank = new GemBank(numberPlayers);
    }

    public List<List<Card>> cardRows() {
        return cardRows;
    }

    public List<Noble> nobles() {
        return nobles;
    }

    public GemBank gemBank() {
        return gemBank;
    }

    private List<List<Card>> initializeCardsRow(BoardType boardType) {

    }

    private List<Noble> initializeNobles(BoardType boardType) {
        switch (boardType) {
            case SIMPLE:
                return List.of();
            case COMPETE:
                return  List.of(/* todo */);
            default:
                throw new ExecutionControl.NotImplementedException();
        }
    }
}
