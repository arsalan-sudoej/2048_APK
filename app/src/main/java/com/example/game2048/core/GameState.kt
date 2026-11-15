package com.example.game2048.core

/**
 * Immutable snapshot of the game state used for rendering and testing.
 */
data class GameState(
    val board: List<List<Int>>,
    val score: Int,
    val bestScore: Int,
    val moveCount: Int,
    val isGameOver: Boolean,
    val isWin: Boolean
) {
    companion object {
        fun newGame(bestScore: Int): GameState {
            val board = List(4) { MutableList(4) { 0 } }
            addRandomTile(board)
            addRandomTile(board)
            return GameState(
                board = board,
                score = 0,
                bestScore = bestScore,
                moveCount = 0,
                isGameOver = false,
                isWin = false
            )
        }

        fun from(board: List<List<Int>>): GameState {
            return GameState(board, 0, 0, 0, false, false)
        }

        private fun addRandomTile(board: List<List<Int>>) {
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
    }
}
