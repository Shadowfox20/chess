package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    @Override
    public int hashCode() {
        return 31 * row + 79 * col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != ChessPosition.class) {
            return false;
        }
        ChessPosition other = (ChessPosition) obj;
        return row == other.getRow() && col == other.getColumn();
    }

    public boolean outOfBounds() {
        return row > 8 || row < 1 || col > 8 || col < 1;
    }
}
