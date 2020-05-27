package chess;

/*
 * @project chess-sytem
 * @author daohn on 27/05/2020
 */
public class ChessException extends RuntimeException {
    private static final long seriaVersionUUID = 1L;

    public ChessException(String msg) {
        super(msg);
    }
}
