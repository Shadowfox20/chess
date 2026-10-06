package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        TeamColor oppColor;
        if (teamTurn == TeamColor.WHITE) {
            oppColor = TeamColor.BLACK;
        } else {
            oppColor = TeamColor.WHITE;
        }
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("there is no piece at " + move.getStartPosition());
        }
        if (teamTurn != piece.getTeamColor()) {
            throw new InvalidMoveException("piece " + piece + " is not the active team color");
        }
        if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("move " + move + " invalid on board");
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            Collection<ChessMove> oppMoves = teamMoves(oppColor);
            ChessPosition endSquare = move.getEndPosition();
            for (ChessMove oppMove : oppMoves) {
                if (oppMove.getEndPosition().equals(endSquare)) {
                    throw new InvalidMoveException("move " + move + " places the king in danger");
                }
            }
        } else if (isInCheck(teamTurn)) {
            if () {
            }
        }
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        teamTurn = oppColor;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor oppColor = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) {
            oppColor = TeamColor.BLACK;
        }
        ChessPosition king = null;
        HashSet<ChessPosition> oppSight = HashSet.newHashSet(8);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece.getTeamColor() == oppColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        oppSight.add(move.getEndPosition());
                    }
                } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    king = position;
                }
            }
        }
        return oppSight.contains(king);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Gets all possible for a team (before applying any game logic, i.e. not accounting for check)
     *
     * @param team the color of the team whose moves are being requested
     * @return a collection of all possible moves
     */
    public Collection<ChessMove> teamMoves(TeamColor team) {
        Collection<ChessMove> teamMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece.getTeamColor() == team) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    teamMoves.addAll(moves);
                }
            }
        }
        return teamMoves;
    }

    @Override
    public int hashCode() {
        return board.hashCode() * 31 + teamTurn.hashCode() * 13;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != ChessGame.class) {
            return false;
        }
        ChessGame other = (ChessGame) obj;
        return teamTurn.equals(other.getTeamTurn()) && board.equals(other.getBoard());
    }
}
