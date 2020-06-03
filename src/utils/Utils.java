package utils;

import chess.ChessPiece;
import chess.Color;

import java.util.List;
import java.util.stream.Collectors;

/*
 * @project chess-sytem
 * @author daohn on 02/06/2020
 */
public class Utils {
    public static List<ChessPiece> filterPiecesByColor(List<ChessPiece> list, Color color) {
        return list.stream()
                .filter(piece -> piece.getColor().equals(color))
                .collect(Collectors.toList());
    }
}
