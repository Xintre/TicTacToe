package com.xintre.tictactoe.gameLogic

enum class UserTurn {
    USER_1,
    USER_2
}

fun UserTurn.getUserName(): String {
    return when (this) {
        UserTurn.USER_1 -> "User 1"
        UserTurn.USER_2 -> "User 2"
    }
}

fun UserTurn.getUsersCellState(): TTTCellState {
    return when (this) {
        UserTurn.USER_1 -> TTTCellState.CIRCLE
        UserTurn.USER_2 -> TTTCellState.CROSS
    }
}
