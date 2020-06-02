package chess;

import boardgame.Position;

/*
 * @project chess-sytem
 * @author daohn on 27/05/2020
 */
public class ChessPosition {
    private final char column;
    private final int row;

    public ChessPosition(char column, int row) {
        if( column < 'a' || column > 'h' || row < 0 || row > 8) {
            throw new ChessException("Error instantiating ChessPosition. Valid position are from a1 to h8");
        }
        this.column = column;
        this.row = row;
    }

    /*
    * matrix_row = 8 - chess_row
    * 'a' - 'a' = 0
    * 'b' - 'a' = 1
    * */
    protected Position toPosition() {
        return new Position((8 - this.row), (this.column - 'a'));
    }

    protected static ChessPosition fromPosition(Position position) {
        return new ChessPosition((char)('a' - position.getColumn()), 8 - position.getRow());
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return "" + column + row;
    }
}
