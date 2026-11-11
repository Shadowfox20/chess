package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class GameDisplay {
    private final ChessGame game;

    public GameDisplay(ChessGame game) {
        this.game = game;
    }

    public void display() {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        for (int i = 1; i < 9; i++) {
            buildSquare(i, gray);
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, j));
                buildSquare(getIcon(piece), getColor(color));
            }
        }
    }

    private String getIcon(ChessPiece piece) {
        if (piece == null) {
            return "";
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
}
