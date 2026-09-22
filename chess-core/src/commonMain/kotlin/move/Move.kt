package xyz.daaren.chess.core.move

import xyz.daaren.chess.core.board.Board
import xyz.daaren.chess.core.board.Square
import xyz.daaren.chess.core.game.GameState
import xyz.daaren.chess.core.piece.PieceType

/**
 * Represents a chess move, including source and destination squares,
 * optional promotion, and special move flags.
 *
 * @property from The starting square of the move.
 * @property to The destination square of the move.
 * @property promotionPieceType The piece type to promote to, if applicable.
 * @property isCapture True if the move is a capture.
 * @property isCastlingKingside True if the move is kingside castling.
 * @property isCastlingQueenside True if the move is queenside castling.
 * @property isEnPassantCapture True if the move is an en passant capture.
 * @property comments Additional move comments, if any
 */
data class Move(
    val from: Square,
    val to: Square,
    val promotionPieceType: PieceType? = null,
    val isCapture: Boolean = false,
    val isCastlingKingside: Boolean = false,
    val isCastlingQueenside: Boolean = false,
    val isEnPassantCapture: Boolean = false,
    val comments: List<String>? = null,
) {
    /**
     * Secondary constructor for moves with promotion specified as a character.
     *
     * @param from The starting square.
     * @param to The destination square.
     * @param promotionChar The character representing the promotion piece type.
     */
    constructor(from: Square, to: Square, promotionChar: Char?) : this(
        from = from,
        to = to,
        promotionPieceType = promotionChar?.let { char ->
            when (char.lowercaseChar()) {
                'q' -> PieceType.QUEEN
                'r' -> PieceType.ROOK
                'b' -> PieceType.BISHOP
                'n' -> PieceType.KNIGHT
                else -> null
            }
        }
    )

    /**
     * Secondary constructor for moves specified by string coordinates.
     *
     * @param from The starting square in algebraic notation (e.g., "e2").
     * @param to The destination square in algebraic notation (e.g., "e4").
     * @param promotionPieceType The piece type to promote to, if applicable.
     */
    constructor(from: String, to: String, promotionPieceType: PieceType? = null) : this(
        from = Square(from),
        to = Square(to),
        promotionPieceType = promotionPieceType
    )

    /**
     * Returns a string representation of the move in coordinate notation,
     * including promotion if applicable.
     */
    override fun toString(): String {
        return "$from$to${promotionPieceType?.name?.first()?.lowercaseChar() ?: ""}"
    }

    /**
     * Returns the move in UCI (Universal Chess Interface) notation.
     *
     * @return The move as a UCI string.
     */
    fun toUci(): String {
        val promotion = promotionPieceType?.name?.first()?.lowercaseChar() ?: ""
        return "${from}${to}$promotion"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Move) return false

        return from == other.from &&
            to == other.to &&
            promotionPieceType == other.promotionPieceType
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + (promotionPieceType?.hashCode() ?: 0)
        return result
    }

    companion object {
        private val legalMoveGenerator = LegalMoveGenerator()

        /**
         * Resolves a UCI (Universal Chess Interface) string into a fully-populated legal [Move]
         * by generating legal moves a single time. Returns `null` if the UCI string does not
         * correspond to a legal move in the given position.
         *
         * Unlike constructing a [Move] from squares and then searching [LegalMoveGenerator]
         * separately, this method avoids redundant move generation.
         *
         * @param uci The UCI string representing the move (e.g., "e2e4", "e7e8q").
         * @param board The current board state.
         * @param gameState The current game state.
         * @return The matching legal [Move] with all flags populated, or `null` if illegal.
         */
        fun fromUci(uci: String, board: Board, gameState: GameState): Move? {
            val fromSquare = Square(uci.substring(0, 2))
            val toSquare = Square(uci.substring(2, 4))
            val promotionPieceType = uci.getOrNull(4)?.let { char ->
                when (char.lowercaseChar()) {
                    'q' -> PieceType.QUEEN
                    'r' -> PieceType.ROOK
                    'b' -> PieceType.BISHOP
                    'n' -> PieceType.KNIGHT
                    else -> null
                }
            }
            return legalMoveGenerator.getAllLegalMoves(board, gameState).firstOrNull {
                it.from == fromSquare && it.to == toSquare && it.promotionPieceType == promotionPieceType
            }
        }
    }

}