package com.xintre.tictactoe.gameLogic

import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xintre.tictactoe.ui.Constants
import com.xintre.tictactoe.ui.theme.ActiveCellBgColor
import com.xintre.tictactoe.ui.theme.InactiveCellBgColor
import com.xintre.tictactoe.ui.theme.WinningCellBgColor


@Composable
fun TTTCell(
    vibrator: Vibrator?,
    gameState: TTTGameState,
    state: TTTCellState,
    cellSizeDp: Int,
    mutateCellState: (newCellState: TTTCellState) -> Unit,
    whoseTurn: UserTurn,
    isWinningCell: Boolean
) {
    val isCellEmpty = state == TTTCellState.EMPTY
    val isGameFinished = gameState == TTTGameState.FINISHED

    return Box(modifier = Modifier.padding(Constants.TTT_CELL_MARGIN_DP.dp)) {
        Box(
            modifier = with(Modifier) {
                size(cellSizeDp.dp)
                    .background(
                        if (isWinningCell)
                            WinningCellBgColor
                        else if (isCellEmpty && !isGameFinished)
                            ActiveCellBgColor
                        else
                            InactiveCellBgColor,
                        shape = RoundedCornerShape(16.dp)
                    )
            }) {
            TextButton(
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.align(alignment = Alignment.Center),
                onClick = {
                    if (isCellEmpty && !isGameFinished) {
                        mutateCellState(whoseTurn.getUsersCellState())
                        vibrator?.vibrate(200)
                    }
                },
            ) {
                Text(
                    state.getUserSymbol(),
                    fontSize = 30.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun TTTCellPreview() {
    return TTTCell(
        vibrator = null,
        gameState = TTTGameState.PLAYING,
        state = TTTCellState.CROSS,
        cellSizeDp = 60,
        mutateCellState = {},
        whoseTurn = UserTurn.USER_1,
        isWinningCell = true
    )
}
