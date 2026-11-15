package com.example.game2048.core

import org.json.JSONArray
import org.json.JSONObject

/**
 * Immutable snapshot of the game state used for rendering and testing.
 */
data class GameState(
    val board: List<List<Int>>,
    val score: Int,
    val bestScore: Int,
    val moveCount: Int,
    val isGameOver: Boolean,
    val isWin: Boolean,
    val mergeCount: Int = 0
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
                isWin = false,
                mergeCount = 0
            )
        }

        fun from(board: List<List<Int>>): GameState {
            return GameState(board, 0, 0, 0, false, false, 0)
        }

        /** Serialize to JSON for auto-save. */
        fun toJson(state: GameState): String {
            val boardArray = JSONArray()
            for (row in state.board) {
                val rowArray = JSONArray()
                for (cell in row) rowArray.put(cell)
                boardArray.put(rowArray)
            }
            val json = JSONObject()
            json.put("board", boardArray)
            json.put("score", state.score)
            json.put("bestScore", state.bestScore)
            json.put("moveCount", state.moveCount)
            json.put("isGameOver", state.isGameOver)
            json.put("isWin", state.isWin)
            json.put("mergeCount", state.mergeCount)
            return json.toString()
        }

        /** Restore from JSON produced by [toJson]; null if corrupt/missing. */
        fun fromJson(text: String?): GameState? {
            if (text.isNullOrBlank()) return null
            return try {
                val json = JSONObject(text)
                val boardArray = json.getJSONArray("board")
                val board = (0 until boardArray.length()).map { r ->
                    val rowArray = boardArray.getJSONArray(r)
                    (0 until rowArray.length()).map { rowArray.getInt(it) }
                }
                GameState(
                    board = board,
                    score = json.optInt("score", 0),
                    bestScore = json.optInt("bestScore", 0),
                    moveCount = json.optInt("moveCount", 0),
                    isGameOver = json.optBoolean("isGameOver", false),
                    isWin = json.optBoolean("isWin", false),
                    mergeCount = json.optInt("mergeCount", 0)
                )
            } catch (e: Exception) {
                null
            }
        }

        private fun addRandomTile(board: List<MutableList<Int>>) {
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
