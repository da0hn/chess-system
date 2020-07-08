package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static utils.Utils.filterPiecesByColor;

public class ChessMatch {

    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkMate;

    private List<Piece> piecesOnTheBoard;
    private List<Piece> capturedPieces;

    public ChessMatch() {
        this.board = new Board(8, 8);
        this.piecesOnTheBoard = new ArrayList<>();
        this.capturedPieces = new ArrayList<>();
        this.turn = 1;
        this.check = false;
        this.checkMate = false;
        this.currentPlayer = Color.WHITE;
        initialSetup();
    }

    public boolean getCheckMate() {
        return this.checkMate;
    }

    public Color getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int getTurn() {
        return this.turn;
    }

    public boolean isCheck() {
        return check;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for(int i = 0; i < board.getRows(); i++) {
            for(int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        // executa o movimento e recebe uma possivel peça capturada
        Piece capturedPiece = makeMove(source, target);
        // testa se o movimento colocou o rei em cheque
        if(testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }
        // testa se o oponente ficou em cheque e atualiza o booleano
        this.check = testCheck(opponent(currentPlayer));
        if(testCheckMate(opponent(currentPlayer))) {
            this.checkMate = true;
        } else {
            nextTurn();
        }
        return (ChessPiece) capturedPiece;
    }

    private void validateTargetPosition(Position source, Position target) {
        if(!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private Piece makeMove(Position source, Position target) {
        Piece piece = board.removePiece(source);
        Optional<Piece> capturedPiece = Optional.ofNullable(board.removePiece(target));
        board.placePiece(piece, target);
        capturedPiece.ifPresent(p -> {
            this.piecesOnTheBoard.remove(p);
            this.capturedPieces.add(p);
        });
        return capturedPiece.orElse(null);
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        Piece p = board.removePiece(target);
        board.placePiece(p, source);
        if(capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
    }

    private void validateSourcePosition(Position position) {
        if(!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece on source position");
        }
        if(currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
            throw new ChessException("The chosen piece is not yours!");
        }
        if(!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    private ChessPiece king(Color color) {
        List<Piece> pieces = piecesOnTheBoard.stream()
                .filter(p -> ((ChessPiece) p).getColor() == color).collect(Collectors.toList());
        for(Piece p : pieces) {
            if(p instanceof King) {
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board");
    }

    private boolean testCheck(Color kingColor) {
        Position kingPosition = king(kingColor).getChessPosition().toPosition();
        List<ChessPiece> opponentPieces = filterPiecesByColor(piecesOnTheBoard.stream()
                                                                      .map(piece -> (ChessPiece) piece)
                                                                      .collect(Collectors.toList()), opponent(kingColor));
        for(ChessPiece piece : opponentPieces) {
            boolean[][] moves = piece.possibleMoves();
            if(moves[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color kingColor) {
        if(!testCheck(kingColor)) return false;
        List<Piece> list = piecesOnTheBoard.stream()
                .filter(p -> ((ChessPiece) p).getColor() == kingColor)
                .collect(Collectors.toList());
        for(Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for(int i=0; i < board.getRows(); i++) {
                for(int j=0; j < board.getColumns(); j++) {
                    if(mat[i][j]) {     // movimento possivel da peça
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(kingColor);
                        undoMove(source, target, capturedPiece);
                        if(!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private Color opponent(Color color) {
        return Color.WHITE == color ? Color.BLACK : Color.WHITE;
    }

    private void initialSetup() {
        placeNewPiece('h', 7, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));

        placeNewPiece('b', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 8, new King(board, Color.BLACK));
    }

}
