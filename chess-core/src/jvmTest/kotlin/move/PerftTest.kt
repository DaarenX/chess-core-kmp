package xyz.daaren.chess.core.move

import xyz.daaren.chess.core.board.Board
import xyz.daaren.chess.core.game.GameState
import xyz.daaren.chess.core.game.GameUtils
import xyz.daaren.chess.core.utils.FenUtils
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class PerftTest {
    private val legalMoveGenerator = LegalMoveGenerator()

    companion object {
        const val FEN_DEFAULT = FenUtils.DEFAULT_FEN
        const val FEN_KIWIPETE = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1"
        const val FEN_POSITION_3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1"
        const val FEN_POSITION_4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"
        const val FEN_POSITION_5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"

        private val EXPECTED_DETAILED_RESULTS = mapOf(
            // Initial Position
            "default_1" to PerftDetailedResults(
                nodes = 20L,
                captures = 0L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 0L,
                checkmates = 0L
            ),
            "default_2" to PerftDetailedResults(
                nodes = 400L,
                captures = 0L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 0L,
                checkmates = 0L
            ),
            "default_3" to PerftDetailedResults(
                nodes = 8_902L,
                captures = 34L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 12L,
                checkmates = 0L
            ),
            "default_4" to PerftDetailedResults(
                nodes = 197_281L,
                captures = 1_576L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 469L,
                checkmates = 8L
            ),
            "default_5" to PerftDetailedResults(
                nodes = 4_865_609L,
                captures = 82_719L,
                enPassant = 258L,
                castles = 0L,
                promotions = 0L,
                checks = 27_351L,
                checkmates = 347L
            ),

            // Position 2: Kiwipete
            "kiwipete_1" to PerftDetailedResults(
                nodes = 48L,
                captures = 8L,
                enPassant = 0L,
                castles = 2L,
                promotions = 0L,
                checks = 0L,
                checkmates = 0L
            ),
            "kiwipete_2" to PerftDetailedResults(
                nodes = 2_039L,
                captures = 351L,
                enPassant = 1L,
                castles = 91L,
                promotions = 0L,
                checks = 3L,
                checkmates = 0L
            ),
            "kiwipete_3" to PerftDetailedResults(
                nodes = 97_862L,
                captures = 17_102L,
                enPassant = 45L,
                castles = 3_162L,
                promotions = 0L,
                checks = 993L,
                checkmates = 1L
            ),
            "kiwipete_4" to PerftDetailedResults(
                nodes = 4_085_603L,
                captures = 757_163L,
                enPassant = 1_929L,
                castles = 128_013L,
                promotions = 15_172L,
                checks = 25_523L,
                checkmates = 43L
            ),
            "kiwipete_5" to PerftDetailedResults(
                nodes = 193_690_690L,
                captures = 35_043_416L,
                enPassant = 73_365L,
                castles = 4_993_637L,
                promotions = 8_392L,
                checks = 3_309_887L,
                checkmates = 30_171L
            ),
            "kiwipete_6" to PerftDetailedResults(
                nodes = 8_031_647_685L,
                captures = 1_558_445_089L,
                enPassant = 3_577_504L,
                castles = 184_513_607L,
                promotions = 56_627_920L,
                checks = 92_238_050L,
                checkmates = 360_003L
            ),

            // Position 3 (Endgame / en passant pin)
            "pos3_1" to PerftDetailedResults(
                nodes = 14L,
                captures = 1L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 2L,
                checkmates = 0L
            ),
            "pos3_2" to PerftDetailedResults(
                nodes = 191L,
                captures = 14L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 10L,
                checkmates = 0L
            ),
            "pos3_3" to PerftDetailedResults(
                nodes = 2_812L,
                captures = 209L,
                enPassant = 2L,
                castles = 0L,
                promotions = 0L,
                checks = 267L,
                checkmates = 0L
            ),
            "pos3_4" to PerftDetailedResults(
                nodes = 43_238L,
                captures = 3_348L,
                enPassant = 123L,
                castles = 0L,
                promotions = 0L,
                checks = 1_680L,
                checkmates = 17L
            ),

            // Position 4 (Promotions, castling & pins)
            "pos4_1" to PerftDetailedResults(
                nodes = 6L,
                captures = 0L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 0L,
                checkmates = 0L
            ),
            "pos4_2" to PerftDetailedResults(
                nodes = 264L,
                captures = 87L,
                enPassant = 0L,
                castles = 6L,
                promotions = 48L,
                checks = 10L,
                checkmates = 0L
            ),
            "pos4_3" to PerftDetailedResults(
                nodes = 9_467L,
                captures = 1_021L,
                enPassant = 4L,
                castles = 0L,
                promotions = 120L,
                checks = 38L,
                checkmates = 22L
            ),
            "pos4_4" to PerftDetailedResults(
                nodes = 422_333L,
                captures = 131_393L,
                enPassant = 0L,
                castles = 7_795L,
                promotions = 60_032L,
                checks = 15_492L,
                checkmates = 5L
            )
        )
    }

    /**
     * Fast perft search to the given depth, computing only node counts.
     *
     * Leaves at depth 1 simply count legal moves without recursing to depth 0.
     */
    fun perftNodes(depth: Int, board: Board, gameState: GameState): Long {
        if (depth == 0) return 1L
        val legalMoves = legalMoveGenerator.getAllLegalMoves(board, gameState)
        if (depth == 1) return legalMoves.size.toLong()

        var totalNodes = 0L
        for (move in legalMoves) {
            val (boardAfter, gameStateAfter) = GameUtils.makeMove(board, gameState, move)
            totalNodes += perftNodes(depth - 1, boardAfter, gameStateAfter)
        }
        return totalNodes
    }

    /**
     * Performs a perft search to the given depth, returning detailed results.
     */
    fun perft(depth: Int, board: Board, gameState: GameState): PerftDetailedResults {
        if (depth == 0) {
            return PerftDetailedResults(nodes = 1L)
        }

        val results = PerftDetailedResults()
        val legalMoves = legalMoveGenerator.getAllLegalMoves(board, gameState)

        if (legalMoves.isEmpty()) {
            return results
        }

        for (move in legalMoves) {
            val (boardAfter, gameStateAfter) = GameUtils.makeMove(board, gameState, move)

            if (depth == 1) {
                results.nodes++
                if (move.isCapture) results.captures++
                if (move.isEnPassantCapture) results.enPassant++
                if (move.isCastlingKingside || move.isCastlingQueenside) results.castles++
                if (move.promotionPieceType != null) results.promotions++

                val opponentColor = gameState.currentPlayer.opposite()
                if (GameUtils.isKingInCheck(opponentColor, boardAfter)) {
                    results.checks++
                    if (legalMoveGenerator.getAllLegalMoves(boardAfter, gameStateAfter).isEmpty()) {
                        results.checkmates++
                    }
                }
            } else {
                val subResults = perft(depth - 1, boardAfter, gameStateAfter)
                results += subResults
            }
        }

        return results
    }

    /**
     * Decomposes perft by first-ply moves, returning a sorted map of UCI move to sub-tree node count.
     */
    fun perftDivide(
        depth: Int,
        board: Board,
        gameState: GameState,
        printOutput: Boolean = false
    ): Map<String, Long> {
        if (depth == 0) return emptyMap()

        val legalMoves = legalMoveGenerator.getAllLegalMoves(board, gameState)
        val divideResults = sortedMapOf<String, Long>()
        var totalNodes = 0L

        for (move in legalMoves) {
            val (boardAfter, gameStateAfter) = GameUtils.makeMove(board, gameState, move)
            val nodes = perftNodes(depth - 1, boardAfter, gameStateAfter)
            val uci = move.toUci()
            divideResults[uci] = nodes
            totalNodes += nodes
            if (printOutput) {
                println("$uci: $nodes")
            }
        }
        if (printOutput) {
            println("Total nodes: $totalNodes")
        }
        return divideResults
    }

    /**
     * Runs a detailed perft test and asserts the result.
     */
    private fun runPerftTest(
        fen: String,
        depth: Int,
        expected: PerftDetailedResults?,
        label: String
    ) {
        val expectedResults = requireNotNull(expected) {
            "Missing expected perft data for $label at depth $depth"
        }
        val (board, gameState) = FenUtils.parseFen(fen)
        val actualResults = perft(depth, board, gameState)
        assertEquals(expectedResults, actualResults, "$label Depth $depth detailed results mismatch")
    }

    /**
     * Runs a fast perft node count test and asserts the result.
     */
    private fun runPerftNodeTest(
        fen: String,
        depth: Int,
        expectedNodes: Long,
        label: String
    ) {
        val (board, gameState) = FenUtils.parseFen(fen)
        val actualNodes = perftNodes(depth, board, gameState)
        assertEquals(expectedNodes, actualNodes, "$label Depth $depth node count mismatch")
    }

    // --- Initial Position Tests ---
    @Test
    fun `perft initial position depth 1`() =
        runPerftTest(FEN_DEFAULT, 1, EXPECTED_DETAILED_RESULTS["default_1"], "Initial Pos")

    @Test
    fun `perft initial position depth 2`() =
        runPerftTest(FEN_DEFAULT, 2, EXPECTED_DETAILED_RESULTS["default_2"], "Initial Pos")

    @Test
    fun `perft initial position depth 3`() =
        runPerftTest(FEN_DEFAULT, 3, EXPECTED_DETAILED_RESULTS["default_3"], "Initial Pos")

    @Test
    fun `perft initial position depth 4`() =
        runPerftTest(FEN_DEFAULT, 4, EXPECTED_DETAILED_RESULTS["default_4"], "Initial Pos")

    @Test
    //@Ignore // Deep test (4.8M nodes) - enable for stress testing
    fun `perft initial position depth 5`() =
        runPerftTest(FEN_DEFAULT, 5, EXPECTED_DETAILED_RESULTS["default_5"], "Initial Pos")

    // --- Kiwipete Tests ---
    @Test
    fun `perft kiwipete depth 1`() =
        runPerftTest(FEN_KIWIPETE, 1, EXPECTED_DETAILED_RESULTS["kiwipete_1"], "Kiwipete")

    @Test
    fun `perft kiwipete depth 2`() =
        runPerftTest(FEN_KIWIPETE, 2, EXPECTED_DETAILED_RESULTS["kiwipete_2"], "Kiwipete")

    @Test
    fun `perft kiwipete depth 3`() =
        runPerftTest(FEN_KIWIPETE, 3, EXPECTED_DETAILED_RESULTS["kiwipete_3"], "Kiwipete")

    @Test
    @Ignore // Deep test (4.0M nodes) - enable for stress testing
    fun `perft kiwipete depth 4`() =
        runPerftTest(FEN_KIWIPETE, 4, EXPECTED_DETAILED_RESULTS["kiwipete_4"], "Kiwipete")

    @Test
    @Ignore // Very deep test (193M nodes) - enable for stress testing
    fun `perft kiwipete depth 5`() =
        runPerftTest(FEN_KIWIPETE, 5, EXPECTED_DETAILED_RESULTS["kiwipete_5"], "Kiwipete")

    // --- Position 3 Tests (Endgame / en passant pin) ---
    @Test
    fun `perft position 3 depth 1`() =
        runPerftTest(FEN_POSITION_3, 1, EXPECTED_DETAILED_RESULTS["pos3_1"], "Position 3")

    @Test
    fun `perft position 3 depth 2`() =
        runPerftTest(FEN_POSITION_3, 2, EXPECTED_DETAILED_RESULTS["pos3_2"], "Position 3")

    @Test
    fun `perft position 3 depth 3`() =
        runPerftTest(FEN_POSITION_3, 3, EXPECTED_DETAILED_RESULTS["pos3_3"], "Position 3")

    // --- Position 4 Tests (Promotions, Castling & Pins) ---
    @Test
    fun `perft position 4 depth 1`() =
        runPerftTest(FEN_POSITION_4, 1, EXPECTED_DETAILED_RESULTS["pos4_1"], "Position 4")

    @Test
    fun `perft position 4 depth 2`() =
        runPerftTest(FEN_POSITION_4, 2, EXPECTED_DETAILED_RESULTS["pos4_2"], "Position 4")

    @Test
    fun `perft position 4 depth 3`() =
        runPerftTest(FEN_POSITION_4, 3, EXPECTED_DETAILED_RESULTS["pos4_3"], "Position 4")

    // --- Position 5 Tests (Promotions while under check) ---
    @Test
    fun `perft position 5 depth 1`() =
        runPerftNodeTest(FEN_POSITION_5, 1, 44L, "Position 5")

    @Test
    fun `perft position 5 depth 2`() =
        runPerftNodeTest(FEN_POSITION_5, 2, 1_486L, "Position 5")

    @Test
    fun `perft position 5 depth 3`() =
        runPerftNodeTest(FEN_POSITION_5, 3, 62_379L, "Position 5")

    // --- Divide Test ---
    @Test
    fun `perft divide initial position depth 1`() {
        val (board, gameState) = FenUtils.parseFen(FEN_DEFAULT)
        val divide = perftDivide(1, board, gameState)
        assertEquals(20, divide.size, "Initial position depth 1 should have 20 moves")
        assertEquals(1L, divide["e2e4"])
        assertEquals(1L, divide["b1c3"])
        assertEquals(20L, divide.values.sum())
    }
}
