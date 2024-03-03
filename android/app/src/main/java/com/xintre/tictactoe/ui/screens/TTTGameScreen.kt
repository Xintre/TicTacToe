package com.xintre.tictactoe.ui.screens

import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.xintre.tictactoe.gameLogic.TTTCell
import com.xintre.tictactoe.gameLogic.TTTCellCoordinates
import com.xintre.tictactoe.gameLogic.TTTCellState
import com.xintre.tictactoe.gameLogic.TTTGameState
import com.xintre.tictactoe.gameLogic.UserTurn
import com.xintre.tictactoe.gameLogic.getUserName
import com.xintre.tictactoe.gameLogic.getUserSymbol
import com.xintre.tictactoe.gameLogic.getUsersCellState
import com.xintre.tictactoe.ui.Constants
import java.util.Locale

fun <T> prepareTTTGrid(
    mapSize: Int,
    renderCell: (row: Int, col: Int) -> T
): List<List<T>> {
    return (0..<mapSize).map { row ->
        (0..<mapSize).map { col ->
            renderCell(row, col)
        }
    }
}

@Composable
fun TTTGameScreen(mapSize: Int) {
    val context = LocalContext.current.applicationContext
    val configuration = LocalConfiguration.current
    val mapState = remember {
        ((prepareTTTGrid(mapSize) { _, _ ->
            TTTCellState.EMPTY
        }).map { rowList ->
            rowList.toMutableStateList()
        }).toMutableStateList()
    }
    var whoseTurn by remember { mutableStateOf(UserTurn.USER_1) }
    var gameState by remember { mutableStateOf(TTTGameState.PLAYING) }
    var gameWinner by remember { mutableStateOf<UserTurn?>(null) }
    var winningIndices by remember { mutableStateOf(listOf<TTTCellCoordinates>()) }
    val vibrator = ContextCompat.getSystemService(
        LocalContext.current.applicationContext,
        Vibrator::class.java
    )

    val screenRowSize =
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            configuration.screenWidthDp
        else
            configuration.screenHeightDp

    val cellSizeDp = screenRowSize / mapSize - Constants.TTT_CELL_MARGIN_DP * 2

    fun finishGameIfApplicable() {
        val allWinningIndices =
            mutableSetOf<TTTCellCoordinates>() // note: there may even be an edge case when two different conditions win at once!

        // check if any row is complete
        var anyRowComplete = false
        rowLoop@ for (rowIndex in 0..<mapSize) {
            val winningRowIndices = mutableListOf<TTTCellCoordinates>()
            var areAllColumnsInThisRowEqual = true

            // the first cell
            val referenceCell = mapState[rowIndex][0]

            columnLoop@ for (colIndex in 0..<mapSize) {
                val cell = mapState[rowIndex][colIndex]
                if (cell != referenceCell || cell == TTTCellState.EMPTY) {
                    areAllColumnsInThisRowEqual = false
                    winningRowIndices.clear()
                    break@columnLoop
                } else {
                    winningRowIndices.add(TTTCellCoordinates(row = rowIndex, col = colIndex))
                }
            }

            if (areAllColumnsInThisRowEqual) {
                anyRowComplete = true
                allWinningIndices.addAll(winningRowIndices)
                break@rowLoop
            }
        }

        // check if any column is complete
        var anyColumnComplete = false
        columnLoop@ for (colIndex in 0..<mapSize) {
            val winningColIndices = mutableListOf<TTTCellCoordinates>()
            var areAllRowsInThisColumnEqual = true

            // the first cell
            val referenceCell = mapState[0][colIndex]

            rowLoop@ for (rowIndex in 0..<mapSize) {
                val cell = mapState[rowIndex][colIndex]
                if (cell != referenceCell || cell == TTTCellState.EMPTY) {
                    areAllRowsInThisColumnEqual = false
                    winningColIndices.clear()
                    break@rowLoop
                } else {
                    winningColIndices.add(TTTCellCoordinates(row = rowIndex, col = colIndex))
                }
            }

            if (areAllRowsInThisColumnEqual) {
                anyColumnComplete = true
                allWinningIndices.addAll(winningColIndices)
                break@columnLoop
            }
        }

        // check if any diagonal is complete
        var leftDiagonalComplete = true
        leftDiagonalLoop@ for (i in 0..<mapSize) {
            val cell = mapState[i][i]
            if (cell != mapState[0][0] || cell == TTTCellState.EMPTY) {
                leftDiagonalComplete = false
                break
            }
            if (areAllColumnsInThisRowEqual) {
                anyRowComplete = true
                allWinningIndices.addAll(winningRowIndices)
                break@rowLoop
            }
        }

        var rightDiagonalComplete = true
        val rightDiagRowIndicesToLoop = (0..<mapSize).toList()
        val rightDiagColIndicesToLoop = rightDiagRowIndicesToLoop.reversed()
        for (i in 0..<mapSize) {
            val rowIndex = rightDiagRowIndicesToLoop[i]
            val colIndex = rightDiagColIndicesToLoop[i]
            // the first cell
            val referenceCell = mapState[0][mapSize - 1]

            val cell = mapState[rowIndex][colIndex]
            if (cell != referenceCell || cell == TTTCellState.EMPTY) {
                rightDiagonalComplete = false
                break
            }
        }

        // check if we have a winner or the game is over with a draw
        if (anyRowComplete || anyColumnComplete || leftDiagonalComplete || rightDiagonalComplete) {
            winningIndices = allWinningIndices.toList()

            val someWinningIndices = winningIndices[0]
            gameWinner =
                if (mapState[someWinningIndices.row][someWinningIndices.col] == UserTurn.USER_1.getUsersCellState()) UserTurn.USER_1 else UserTurn.USER_2

            gameState = TTTGameState.FINISHED

            vibrator?.vibrate(200)
            Handler(Looper.myLooper()!!).postDelayed({
                vibrator?.vibrate(350)

                Handler(Looper.myLooper()!!).postDelayed({
                    vibrator?.vibrate(200)
                }, 200)
            }, 400)

            Toast.makeText(
                context,
                "Game finished - ${gameWinner!!.getUserName()} wins!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val allCellsFilled = mapState.flatten().all { cell ->
                cell != TTTCellState.EMPTY
            }

            if (allCellsFilled) {
                vibrator?.vibrate(700)

                winningIndices = listOf()
                gameWinner = null

                gameState = TTTGameState.FINISHED

                Toast.makeText(
                    context,
                    "Game finished - it's a draw!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val banner = if (gameState == TTTGameState.PLAYING)
        "It's ${whoseTurn.getUserName()}'s turn"
    else when (gameWinner) {
        null -> "The game was a draw \uD83E\uDD1D"
        else -> "\uD83C\uDFC6 ${
            gameWinner!!.getUserName()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } won the game \uD83C\uDFC6"
    }

    return Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Text(
                text = banner,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        if (gameState == TTTGameState.PLAYING) {
            Row {
                Text(
                    text = "Let's make ${
                        whoseTurn.getUsersCellState().getUserSymbol()
                    }s rain! \uD83D\uDCA7☔",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 0.dp, 30.dp),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        mapState.forEachIndexed { row, rowList ->
            Row(
                modifier = with(Modifier) {
                    fillMaxWidth()
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                rowList.forEachIndexed { col, state ->
                    val isWinningCell = winningIndices.contains(
                        TTTCellCoordinates(
                            row = row,
                            col = col
                        )
                    )

                    TTTCell(
                        vibrator = vibrator,
                        gameState = gameState,
                        state = state,
                        cellSizeDp = cellSizeDp,
                        whoseTurn = whoseTurn,
                        isWinningCell = isWinningCell,
                        mutateCellState = { newCellState ->
                            mapState[row][col] = newCellState

                            whoseTurn = when (whoseTurn) {
                                UserTurn.USER_1 -> UserTurn.USER_2
                                UserTurn.USER_2 -> UserTurn.USER_1
                            }

                            finishGameIfApplicable()
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TTTGamePreview() {
    TTTGameScreen(mapSize = 4)
}