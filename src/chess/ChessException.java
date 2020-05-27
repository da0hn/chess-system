package chess;

import boardgame.BoardException;

/*
 * @project chess-sytem
 * @author daohn on 27/05/2020
 */
public class ChessException extends BoardException {
    private static final long seriaVersionUUID = 1L;

    public ChessException(String msg) {
        super(msg);
    }
}
