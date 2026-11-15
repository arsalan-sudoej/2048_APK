package com.example.game2048.core

/**
 * Pure game engine: board moves, merges, scoring and spawn logic.
 * No Android dependencies - fully unit-testable.
 */
object GameEngine {

    const val WIN_TILE = 2048

    /**
     * Attempt a move. Returns the new state, or null if the move is invalid
     * (nothing moved or merged).
     */
    fun move(state: GameState, direction: Direction): GameState? {
        if (state.isGameOver) return null

        val transformed = when (direction) {
            Direction.LEFT -> state.board.map { it.toMutableList() }
            Direction.RIGHT -> state.board.map { it.reversed().toMutableList() }
            Direction.UP -> transpose(state.board).map { it.toMutableList() }
            Direction.DOWN -> transpose(state.board).map { it.reversed().toMutableList() }
        }

        var scoreGain = 0
        var merges = 0

        // Slide each line left and merge equal neighbours once.
        val slid = transformed.map { line ->
            val nonZero = line.filter { it != 0 }
            val result = nonZero.toMutableList()
            var i = 0
            while (i < result.size - 1) {
                if (result[i] == result[i + 1]) {
                    val merged = result[i] * 2
                    result[i] = merged
                    result.removeAt(i + 1)
                    scoreGain += merged
                    merges++
                }
                i++
            }
            while (result.size < line.size) result.add(0)
            result
        }

        // Transform back to the original orientation.
        val newBoard = when (direction) {
            Direction.LEFT -> slid
            Direction.RIGHT -> slid.map { it.reversed() }
            Direction.UP -> transpose(slid)
            Direction.DOWN -> transpose(slid).map { it.reversed() }
        }

        if (newBoard == state.board) return null

        val mutable = newBoard.map { it.toMutableList() }
        spawnRandomTile(mutable)

        val newScore = state.score + scoreGain
        val isWin = state.isWin || hasTile(mutable, WIN_TILE)

        return state.copy(
            board = mutable,
            score = newScore,
            bestScore = maxOf(state.bestScore, newScore),
            moveCount = state.moveCount + 1,
            mergeCount = state.mergeCount + merges,
            isGameOver = isGameOver(mutable),
            isWin = isWin
        )
    }

    fun spawnRandomTile(board: List<MutableList<Int>>) {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (r in board.indices) {
            for (c in board[r].indices) {
                if (board[r][c] == 0) empty.add(r to c)
            }
        }
        if (empty.isEmpty()) return
        val (r, c) = empty.random()
        board[r][c] = if ((0..9).random() == 0) 4 else 2
    }

    fun hasTile(board: List<List<Int>>, value: Int): Boolean {
        return board.any { row -> row.contains(value) }
    }

    fun highestTile(board: List<List<Int>>): Int {
        return board.maxOfOrNull { it.maxOrNull() ?: 0 } ?: 0
    }

    fun isGameOver(board: List<List<Int>>): Boolean {
        if (board.any { row -> row.any { it == 0 } }) return false
        for (r in 0..3) {
            for (c in 0..3) {
                val v = board[r][c]
                if (r < 3 && board[r + 1][c] == v) return false
                if (c < 3 && board[r][c + 1] == v) return false
            }
        }
        return true
    }

    private fun transpose(board: List<List<Int>>): List<List<Int>> {
        return List(4) { r -> List(4) { c -> board[c][r] } }
    }
}

enum class Direction { LEFT, RIGHT, UP, DOWN }
