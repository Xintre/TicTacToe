package com.xintre.tictactoe.gameLogic

enum class TTTCellState {
    EMPTY, CROSS, CIRCLE
}

fun TTTCellState.getUserSymbol(): String {
    return when (this) {
        TTTCellState.EMPTY -> {
            " "
        }

        TTTCellState.CROSS -> {
            "❌"
        }

        TTTCellState.CIRCLE -> {
            "⭕"
        }
    }
}
