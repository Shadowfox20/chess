package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<ChessMove>();
        var currentCol = myPosition.getColumn();
        var currentRow = myPosition.getRow();
        ChessPosition dest;
        ChessPiece target;
        switch(type) {
            case PAWN:
                var targetRow = currentRow;
                if (pieceColor == ChessGame.TeamColor.WHITE) {
                    targetRow += 1;
                } else {
                    targetRow -= 1;
                }
                ArrayList<ChessPiece.PieceType> promotions = new ArrayList<>(1);
                promotions.add(null);
                if (targetRow == 1 || targetRow == 8) {
                    promotions.set(0, PieceType.KNIGHT);
                    promotions.add(PieceType.BISHOP);
                    promotions.add(PieceType.ROOK);
                    promotions.add(PieceType.QUEEN);
                }
                dest = new ChessPosition(targetRow, currentCol);
                target = board.getPiece(dest);
                if (target == null) {
                    for (PieceType promotion : promotions) {
                        validMoves.add(new ChessMove(myPosition, dest, promotion));
                    }
                }
                if (currentCol != 1) {
                    dest = new ChessPosition(targetRow, currentCol - 1);
                    target = board.getPiece(dest);
                    if (target != null && target.getTeamColor() != pieceColor) {
                        for (PieceType promotion : promotions) {
                            validMoves.add(new ChessMove(myPosition, dest, promotion));
                        }
                    }
                }
                if (currentCol != 8) {
                    dest = new ChessPosition(targetRow, currentCol + 1);
                    target = board.getPiece(dest);
                    if (target != null && target.getTeamColor() != pieceColor) {
                        for (PieceType promotion : promotions) {
                            validMoves.add(new ChessMove(myPosition, dest, promotion));
                        }
                    }
                }
            case KNIGHT:
                ChessPosition[] sight = {
                    new ChessPosition(currentRow + 1, currentCol + 2),
                    new ChessPosition(currentRow + 1, currentCol - 2),
                    new ChessPosition(currentRow - 1, currentCol + 2),
                    new ChessPosition(currentRow - 1, currentCol - 2),
                    new ChessPosition(currentRow + 2, currentCol + 1),
                    new ChessPosition(currentRow + 2, currentCol - 1),
                    new ChessPosition(currentRow - 2, currentCol + 1),
                    new ChessPosition(currentRow - 2, currentCol - 1)
                };
                for (int i=0; i<8; i++) {
                    dest = sight[i];
                    if (!dest.inBounds()) {
                        continue;
                    }
                    target = board.getPiece(dest);
                    if (target == null || target.getTeamColor() != pieceColor) {
                        validMoves.add(new ChessMove(myPosition, dest, null));
                    }
                }
            case BISHOP:
                int[] bishopVertical = {-1,-1,1,1};
                int[] bishopHorizontal = {-1,1,-1,1};
                validMoves = lineOfSight(board, myPosition, bishopVertical, bishopHorizontal);
            case ROOK:
                int[] rookVertical = {-1,1,0,0};
                int[] rookHorizontal = {0,0,-1,1};
                validMoves = lineOfSight(board, myPosition, rookVertical, rookHorizontal);
            case QUEEN:
                int[] queenVertical = {-1,-1,-1,0,1,1,1,0};
                int[] queenHorizontal = {-1,0,1,1,1,0,-1,-1};
                validMoves = lineOfSight(board, myPosition, queenVertical, queenHorizontal);
        }
        return validMoves;
    }

    private HashSet<ChessMove> lineOfSight(ChessBoard board, ChessPosition myPosition, int[] vertical, int[] horizontal) {
        int len = Math.min(horizontal.length, vertical.length);
        int currentCol = myPosition.getColumn();
        int currentRow = myPosition.getRow();
        HashSet<ChessMove> validMoves = new HashSet<>();
        for (int i=0; i<len; i++) {
            int v = vertical[i];
            int h = horizontal[i];
            int x = currentCol;
            int y = currentRow;
            while (true) {
                x += h;
                y += v;
                ChessPosition dest = new ChessPosition(x, y);
                if (!dest.inBounds()) {
                    break;
                }
                ChessPiece target = board.getPiece(dest);
                if (target == null) {
                    validMoves.add(new ChessMove(myPosition, dest, null));
                    continue;
                }
                if (target.getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, dest, null));
                }
                break;
            }
        }
        return validMoves;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == ChessPiece.class) {
            return this.hashCode() == obj.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (31 * pieceColor.hashCode()) + (79 * type.hashCode());
    }
}
