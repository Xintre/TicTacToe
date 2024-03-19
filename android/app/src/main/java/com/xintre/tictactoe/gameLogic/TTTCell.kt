package com.xintre.tictactoe.gameLogic

import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xintre.tictactoe.ui.Constants
import com.xintre.tictactoe.ui.dpToSp
import com.xintre.tictactoe.ui.theme.ActiveCellBgColor
import com.xintre.tictactoe.ui.theme.InactiveCellBgColor
import com.xintre.tictactoe.ui.theme.WinningCellBgColor


@Suppress("LongParameterList", "FunctionNaming")
@Composable
fun TTTCell(
    vibrator: Vibrator?,
    gameState: TTTGameState,
    state: TTTCellState,
    cellSizeDp: Float,
    mutateCellState: (newCellState: TTTCellState) -> Unit,
    whoseTurn: MutableState<UserTurn>,
    isWinningCell: Boolean
) {
    val isCellEmpty = state == TTTCellState.EMPTY
    val isGameFinished = gameState == TTTGameState.FINISHED
    val context = LocalContext.current

    return Box(modifier = Modifier.padding(Constants.TTT_CELL_MARGIN_DP.dp)) {
        val shapeMask = RoundedCornerShape(16.dp)

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
                        shape = shapeMask
                    )
                    .clip(shapeMask)
                    .clickable {
                        if (isCellEmpty && !isGameFinished) {
                            mutateCellState(whoseTurn.value.getUsersCellState())
                            vibrator?.vibrate(200)
                        }
                    }
            }
        ) {
            val fontSizeDp = (cellSizeDp * 0.6f)

            Text(
                state.getUserSymbol(),
                style = TextStyle(
                    fontSize = dpToSp(
                        fontSizeDp,
                        context
                    ).sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .offset(y = -(fontSizeDp * 0.05f).dp),
            )
        }
    }
}

@Preview
@Composable
fun TTTCellPreview() {
    val whoseTurn = remember { mutableStateOf(UserTurn.USER_1) }

    return TTTCell(
        vibrator = null,
        gameState = TTTGameState.PLAYING,
        state = TTTCellState.CROSS,
        cellSizeDp = 40f,
        mutateCellState = {},
        whoseTurn = whoseTurn,
        isWinningCell = true
    )
}
