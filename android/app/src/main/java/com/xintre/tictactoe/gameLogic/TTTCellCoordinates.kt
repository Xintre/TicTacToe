package com.xintre.tictactoe.gameLogic

data class TTTCellCoordinates(val row: Int, val col: Int) {
    override fun hashCode(): Int {
        return (row to col).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TTTCellCoordinates -> other.row == row && other.col == col
            else -> false
        }
    }
}
