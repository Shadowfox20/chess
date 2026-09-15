package chess;

import java.awt.font.FontRenderContext;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board = new ChessPiece[8][8];
    private final ChessPiece.PieceType[] backRow = {
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.KING,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.ROOK
    };

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i=0; i<8; i++) {
            board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, backRow[i]);
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, backRow[i]);
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                hash = hash * 31 + ((board[i][j] != null) ? board[i][j].hashCode() : -1);
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != ChessBoard.class) {
            return false;
        }
        ChessBoard other = (ChessBoard) obj;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece thisPiece = getPiece(pos);
                ChessPiece otherPiece = other.getPiece(pos);
                if (thisPiece == null) {
                    if (otherPiece != null) {
                        return false;
                    }
                } else if (!thisPiece.equals(otherPiece)) {
                    return false;
                }
            }
        }
        return true;
    }
}
