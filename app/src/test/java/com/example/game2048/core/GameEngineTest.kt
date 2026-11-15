package com.example.game2048.core

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
    fun invalidMoveReturnsNull() {
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 2, 0, 0),
            intArrayOf(0, 0, 2, 0),
            intArrayOf(0, 0, 0, 2)
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
}
