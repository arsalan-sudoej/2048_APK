package com.example.game2048.core

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    private fun boardOf(vararg rows: IntArray): List<List<Int>> = rows.map { it.toList() }

    @Test
    fun moveLeftMergesEqualTiles() {
        val board = boardOf(
            intArrayOf(2, 2, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.LEFT)

        assertNotNull(moved)
        assertEquals(4, moved!!.board[0][0])
        assertEquals(4, moved.score)
    }

    @Test
    fun moveRightSlidesTiles() {
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.RIGHT)

        assertNotNull(moved)
        assertEquals(2, moved!!.board[0][3])
    }

    @Test
    fun diagonalAntiBoardStillMovesLeft() {
        // Diagonal tiles DO move left - this is a valid move.
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 2, 0, 0),
            intArrayOf(0, 0, 2, 0),
            intArrayOf(0, 0, 0, 2)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.LEFT)

        assertNotNull(moved)
        assertTrue(moved!!.moveCount == 1)
    }

    @Test
    fun invalidMoveReturnsNull() {
        // A board where nothing can slide or merge LEFT.
        val board = boardOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2)
        )
        val state = GameState.from(board)
        assertNull(GameEngine.move(state, Direction.LEFT))
    }

    @Test
    fun fullBoardNoMergesIsGameOver() {
        val board = boardOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2)
        )
        assertTrue(GameEngine.isGameOver(board))
    }

    @Test
    fun fullBoardWithAdjacentMergeIsNotGameOver() {
        val board = boardOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 4)
        )
        assertFalse(GameEngine.isGameOver(board))
    }

    @Test
    fun winDetectionFinds2048() {
        val board = boardOf(
            intArrayOf(2048, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        assertTrue(GameEngine.hasTile(board, GameEngine.WIN_TILE))
    }

    @Test
    fun scoreAccumulatesFromMerges() {
        val board = boardOf(
            intArrayOf(2, 2, 4, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.LEFT)
        assertEquals(12, moved!!.score)
    }

    // -- Merge counting (stats + coins) --

    @Test
    fun mergeCountIncrementsPerMerge() {
        val board = boardOf(
            intArrayOf(2, 2, 4, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.LEFT)
        assertEquals(2, moved!!.mergeCount)
    }

    // -- Undo snapshot semantics --

    @Test
    fun newStatesAreIndependentCopies() {
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.LEFT)
        assertNotNull(moved)
        // The original state must be unchanged after a move (undo safety).
        assertEquals(2, state.board[0][0])
    }

    // -- JSON round-trip (autosave) --

    @Test
    fun jsonRoundTripPreservesState() {
        val board = boardOf(
            intArrayOf(2, 4, 8, 16),
            intArrayOf(32, 64, 128, 256),
            intArrayOf(512, 1024, 2, 4),
            intArrayOf(8, 16, 32, 64)
        )
        val state = GameState(
            board = board,
            score = 1234,
            bestScore = 5678,
            moveCount = 42,
            isGameOver = false,
            isWin = false,
            mergeCount = 7
        )
        val restored = GameState.fromJson(GameState.toJson(state))
        assertNotNull(restored)
        assertEquals(state, restored)
    }

    @Test
    fun jsonCorruptReturnsNull() {
        assertNull(GameState.fromJson("not json at all {{{"))
        assertNull(GameState.fromJson(null))
        assertNull(GameState.fromJson(""))
    }

    // -- Spawn randomness sanity --

    @Test
    fun spawnFillsEmptyCellWith2Or4() {
        val board = List(4) { MutableList(4) { 0 } }
        GameEngine.spawnRandomTile(board)
        val values = board.flatten().filter { it != 0 }
        assertEquals(1, values.size)
        assertTrue(values[0] == 2 || values[0] == 4)
    }

    @Test
    fun newGameSpawnsTwoTiles() {
        val state = GameState.newGame(bestScore = 100)
        val nonZero = state.board.flatten().count { it != 0 }
        assertEquals(2, nonZero)
        assertEquals(100, state.bestScore)
    }
}
