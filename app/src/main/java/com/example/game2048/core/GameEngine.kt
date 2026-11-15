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

        val board = state.board.map { it.toMutableList() }
        var moved = false
        var scoreGain = 0
        val mergedTiles = mutableListOf<Pair<Int, Int>>>()

        fun slideLine(line: List<Int>): List<Int> {
            val nonZero = line.filter { it != 0 }
            val result = nonZero.toMutableList()
            var i = 0
            while (i < result.size - 1) {
                if (result[i] == result[i + 1]) {
                    val merged = result[i] * 2
                    result[i] = merged
                    result.removeAt(i + 1)
                    scoreGain += merged
                    moved = true
                }
                i++
            }
            while (result.size < 4) result.add(0)
            return result
        }

        fun transform(board: List<MutableList<Int>>, d: Direction): List<List<Int>> {
            return when (d) {
                Direction.LEFT -> board
                Direction.RIGHT -> board.map { it.reversed() }
                Direction.UP -> transpose(board)
                Direction.DOWN -> transpose(board).map { it.reversed() }
            }
        }

        fun transpose(b: List<MutableList<Int>>): List<List<Int>> {
            return List(4) { r -> List(4) { c -> b[c][r] } }
        }

        val (transformed, reversed, transposed) = when (direction) {
            Direction.LEFT -> Triple(board, false, false)
            Direction.RIGHT -> Triple(board.map { it.reversed().toMutableList() }, true, false)
            Direction.UP -> Triple(transpose(board).map { it.toMutableList() }, false, true)
            Direction.DOWN -> {
                val t = transpose(board).map { it.reversed() }.map { it.toMutableList() }
                Triple(t, true, true)
            }
        }

        val slid = transformed.map { slideLine(it) }.map { it.toMutableList() }

        val newBoard = when {
            !reversed && !transposed -> slid
            reversed && !transposed -> slid.map { it.reversed().toMutableList() }
            !reversed && transposed -> transpose(slid).map { it.toMutableList() }
            else -> transpose(slid.map { it.reversed() }).map { it.toMutableList() }
        }

        if (!moved) return null

        spawnRandomTile(newBoard)

        val newScore = state.score + scoreGain
        val isWin = state.isWin || hasTile(newBoard, WIN_TILE)

        return state.copy(
            board = newBoard,
            score = newScore,
            bestScore = maxOf(state.bestScore, newScore),
            moveCount = state.moveCount + 1,
            isGameOver = isGameOver(newBoard),
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

    fun isGameOver(board: List<List<Int>>): Boolean {
        val full = board.all { row -> row.all { it != 0 } }
        if (!full) return false
        // Any adjacent equal pair means a merge is still possible.
        for (r in 0..3) {
            for (c in 0..3) {
                val v = board[r][c]
                if (r < 3 && board[r + 1][c] == v) return false
                if (c < 3 && board[r][c + 1] == v) return false
            }
        }
        return true
    }
}

enum class Direction { LEFT, RIGHT, UP, DOWN }
