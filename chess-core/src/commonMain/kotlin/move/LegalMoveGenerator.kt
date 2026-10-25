package xyz.daaren.chess.core.move

import xyz.daaren.chess.core.board.Board
import xyz.daaren.chess.core.board.File
import xyz.daaren.chess.core.board.Square
import xyz.daaren.chess.core.game.GameState
import xyz.daaren.chess.core.game.GameUtils
import xyz.daaren.chess.core.piece.PieceColor

/**
 * Generates all legal moves for the current player, filtering out pseudo-legal moves
 * that would leave the player's king in check.
 */
internal class LegalMoveGenerator {
    /**
     * Returns a list of all legal moves for the current player.
     *
     * @param board The current board state.
     * @param currentPlayerGameState The current game state for the player.
     * @return List of legal moves.
     */
    fun getAllLegalMoves(
        board: Board,
        currentPlayerGameState: GameState,
    ): List<Move> {
        val currentPlayerColor = currentPlayerGameState.currentPlayer

        val pseudoLegalMoves = PseudoLegalMoveGenerator.getAllPseudoLegalMoves(board, currentPlayerGameState)

        // Compute once whether the king is currently in check (used to reject castling)
        val isInCheck = GameUtils.isKingInCheck(currentPlayerColor, board)

        return pseudoLegalMoves
            .asSequence()
            .filter { move ->
                if (move.isCastlingKingside || move.isCastlingQueenside) {
                    // Can't castle out of check; path must be safe; king must not end in check
                    !isInCheck &&
                        isCastlingPathSafe(move, currentPlayerColor, board) &&
                        !GameUtils.isKingInCheck(currentPlayerColor, board.applyMove(move))
                } else {
                    // Only need the resulting board to verify the king isn't left in check
                    !GameUtils.isKingInCheck(currentPlayerColor, board.applyMove(move))
                }
            }.toList()
    }

    /**
     * Checks if the castling path is safe (i.e., not attacked by the opponent).
     * The king's starting square (e-file) is NOT included here because the caller
     * already verifies that the king is not in check before calling this method.
     *
     * @param castlingMove The castling move to check.
     * @param kingColor The color of the king.
     * @param board The current board state.
     * @return True if the castling path is safe, false otherwise.
     */
    private fun isCastlingPathSafe(
        castlingMove: Move,
        kingColor: PieceColor,
        board: Board,
    ): Boolean {
        val kingRank = castlingMove.from.rank
        val pathSquares =
            when {
                castlingMove.isCastlingKingside -> {
                    listOf(
                        Square(File('f'), kingRank),
                        Square(File('g'), kingRank),
                    )
                }

                castlingMove.isCastlingQueenside -> {
                    listOf(
                        Square(File('d'), kingRank),
                        Square(File('c'), kingRank),
                    )
                }

                else -> {
                    return false
                }
            }

        return pathSquares.none { square ->
            GameUtils.isSquareAttackedBy(square, kingColor.opposite(), board)
        }
    }
}
