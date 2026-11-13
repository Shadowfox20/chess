package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import jdk.swing.interop.DropTargetContextWrapper;

import java.util.Scanner;

public class ChessDisplay {
    private final ChessGame game;
    private String gray = "30;47;1";

    public ChessDisplay(ChessGame game) {
        this.game = game;
    }

    public void display(String playerColor) {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.flush();
        for (int i = 0; i < 10; i++) {
            buildSquare(EscapeSequences.EMPTY, gray);
        }
        System.out.println();
        int start = 1;
        int end = 9;
        int dir = 1;
        if (playerColor.equals("BLACK")) {
            start = 8;
            end = 0;
            dir = -1;
        }
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        for (int i = start; (i < end && dir > 0) || (i > end && dir < 0); i += dir) {
            buildSquare(" " + Character.toString(0xFF19 - i) + " ", gray);
            for (int j = start; (j < end && dir > 0) || (j > end && dir < 0); j += dir) {
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(9 - i, j));
                buildSquare(getIcon(piece), getColor(color, piece));
                color = changeColor(color);
            }
            color = changeColor(color);
            buildSquare(EscapeSequences.EMPTY, gray);
            System.out.println();
        }
        String[] row = {" Ａ ", " Ｂ ", " Ｃ ", " Ｄ ", " Ｅ ", " Ｆ ", " Ｇ ", " Ｈ "};
        buildSquare(EscapeSequences.EMPTY, gray);
        for (int i = start - 1; (i < end - 1 && dir > 0) || (i > end - 1 && dir < 0); i += dir) {
            buildSquare(row[i], gray);
        }
        buildSquare(EscapeSequences.EMPTY, gray);
        System.out.println();
    }

    private String getIcon(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case PAWN -> {
                    return EscapeSequences.WHITE_PAWN;
                }
                case KNIGHT -> {
                    return EscapeSequences.WHITE_KNIGHT;
                }
                case BISHOP -> {
                    return EscapeSequences.WHITE_BISHOP;
                }
                case ROOK -> {
                    return EscapeSequences.WHITE_ROOK;
                }
                case QUEEN -> {
                    return EscapeSequences.WHITE_QUEEN;
                }
                case KING -> {
                    return EscapeSequences.WHITE_KING;
                }
            }
        }
        switch (piece.getPieceType()) {
            case PAWN -> {
                return EscapeSequences.BLACK_PAWN;
            }
            case KNIGHT -> {
                return EscapeSequences.BLACK_KNIGHT;
            }
            case BISHOP -> {
                return EscapeSequences.BLACK_BISHOP;
            }
            case ROOK -> {
                return EscapeSequences.BLACK_ROOK;
            }
            case QUEEN -> {
                return EscapeSequences.BLACK_QUEEN;
            }
            case KING -> {
                return EscapeSequences.BLACK_KING;
            }
        }
        return "";
    }

    private String getColor(ChessGame.TeamColor background, ChessPiece piece) {
        if (background == ChessGame.TeamColor.WHITE) {
            if (piece == null || piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return "37;107;1";
            } else {
                return "30;107;1";
            }
        } else {
            if (piece == null || piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return "15;40;1";
            } else {
                return "37;40;1";
            }
        }
    }

    private ChessGame.TeamColor changeColor(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void buildSquare(String piece, String color) {
        System.out.print("\u001b[" + color + "m" + piece + "\u001b[0m");
    }

}
