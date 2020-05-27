package app;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Scanner;

public class Program {

    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        ChessMatch match = new ChessMatch();
        while(true) {
            UI.printBoard(match.getPieces());
            System.out.println();
            System.out.print("Source: ");
            ChessPosition source = UI.readChessPosition(sc);
            System.out.println();
            System.out.print("Target: ");
            ChessPosition target = UI.readChessPosition(sc);
            ChessPiece capturedPiece = match.performChessMove(source, target);
        }

    }

}
