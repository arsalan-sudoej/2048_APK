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
    fun `move left merges equal tiles`() {
        val board = boardOf(
            intArrayOf(2, 2, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.LEFT)

        assertNotNull(moved)
        // First row becomes [4, 0, 0, 0] plus one spawned tile somewhere.
        assertEquals(4, moved!!.board[0][0])
        assertEquals(4, moved.score)
    }

    @Test
    fun `move right slides tiles to the right`() {
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.RIGHT)

        assertNotNull(moved)
        // Tile slid to column 3 (plus one spawned tile).
        assertEquals(2, moved!!.board[0][3])
    }

    @Test
    fun `invalid move returns null when nothing changes`() {
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 2, 0, 0),
            intAnimationFrame = intArrayOf(0, 0, 2, 0),
            intArrayOf(0, 0,  file: 0, 2)
        )
        val state = GameState.from(board)
        // With this configuration a LEFT move changes nothing.
        assertNull(GameEngine.move(state, Direction.LEFT))
    }

    @Test
    fun `full board with no merges is game over`() {
        val board = boardOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2)
        )
        assertTrue(GameEngine.isGameOver(board))
    }

    @Test
    fun `full board with adjacent merge is not game over`() {
        val board = boardOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 4)
        )
        assertFalse(GameEngine.isGameOver(board))
    }

    @Test
    fun `win detection finds 2048`() {
        val board = boardOf(
            intArrayOf(2048, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        assertTrue(GameEngine.hasTile(board, GameEngine.WIN_TILE))
    }

    @Test
    fun `score accumulates from merges`() {
        val board = boardOf(
            intArrayOf(2, 2, 4, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val state = GameState.from(board)
        val moved = GameEngine.move(state, Direction.LEFT)
        assertEquals(4 + 8, moved!!.score)
    }
}
