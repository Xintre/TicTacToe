package com.xintre.tictactoe.ui.screens

import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.xintre.tictactoe.ui.pxToDp
import java.util.Locale


const val LOG_TAG = "TTTGameScreen"

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

fun createEmptyMap(mapSize: Int): SnapshotStateList<SnapshotStateList<TTTCellState>> {
    return ((prepareTTTGrid(mapSize) { _, _ ->
        TTTCellState.EMPTY
    }).map { rowList ->
        rowList.toMutableStateList()
    }).toMutableStateList()
}

@Composable
fun TTTGameScreen(mapSize: Int, openMenuScreen: () -> Unit) {
    val context = LocalContext.current.applicationContext
    val mapState = rememberSaveable(
        key = "mapState", saver = listSaver(
            save = { stateList ->
                stateList.flatMap {
                    it.toList()
                }.toList()
            },
            restore = { flattened ->
                val reconstruction = mutableListOf<List<TTTCellState>>()

                var i = 0
                while (i < flattened.size) {
                    reconstruction.add(flattened.subList(i, i + mapSize))
                    i += mapSize
                }

                reconstruction.map { row ->
                    row.toMutableStateList()
                }.toMutableStateList()
            }
        )
    ) { createEmptyMap(mapSize) }
    val whoseTurn = rememberSaveable(key = "whoseTurn") { mutableStateOf(UserTurn.USER_1) }
    var gameState by rememberSaveable(key = "gameState") { mutableStateOf(TTTGameState.PLAYING) }
    var gameWinner by rememberSaveable(key = "gameWinner") { mutableStateOf<UserTurn?>(null) }
    var menuSizeDp by remember { mutableFloatStateOf(0f) }
    var rootContainerWidthDp by remember { mutableFloatStateOf(0f) }
    var rootContainerHeightDp by remember { mutableFloatStateOf(0f) }
    var winningIndices by rememberSaveable(key = "winningIndices") { mutableStateOf(listOf<TTTCellCoordinates>()) }
    val vibrator = ContextCompat.getSystemService(
        LocalContext.current.applicationContext,
        Vibrator::class.java
    )

    val cellSizeDp = Math.min(
        // by width
        rootContainerWidthDp.toFloat() / mapSize.toFloat() - Constants.TTT_CELL_MARGIN_DP * 2,
        // by height
        (rootContainerHeightDp - menuSizeDp).toFloat() / mapSize.toFloat() - Constants.TTT_CELL_MARGIN_DP * 2
    )

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
        val winningDiagLIndices = mutableListOf<TTTCellCoordinates>()
        leftDiagonalLoop@ for (i in 0..<mapSize) {
            var areAllColumnsInThisDiagEqual = true
            val cell = mapState[i][i]
            if (cell != mapState[0][0] || cell == TTTCellState.EMPTY) {
                leftDiagonalComplete = false
                winningDiagLIndices.clear()
                break
            } else {
                winningDiagLIndices.add(TTTCellCoordinates(row = i, col = i))
            }
        }
        if (leftDiagonalComplete) {
            allWinningIndices.addAll(winningDiagLIndices)
        }

        var rightDiagonalComplete = true
        val winningDiagRIndicies = mutableListOf<TTTCellCoordinates>()
        val rightDiagRowIndicesToLoop = (0..<mapSize).toList()
        val rightDiagColIndicesToLoop = rightDiagRowIndicesToLoop.reversed()
        rightDiagonalLoop@ for (i in 0..<mapSize) {
            val rowIndex = rightDiagRowIndicesToLoop[i]
            val colIndex = rightDiagColIndicesToLoop[i]
            // the first cell
            val referenceCell = mapState[0][mapSize - 1]

            val cell = mapState[rowIndex][colIndex]
            if (cell != referenceCell || cell == TTTCellState.EMPTY) {
                rightDiagonalComplete = false
                winningDiagRIndicies.clear()
                break@rightDiagonalLoop
            } else {
                winningDiagRIndicies.add(TTTCellCoordinates(row = rowIndex, col = colIndex))
            }
        }
        if (rightDiagonalComplete) {
            allWinningIndices.addAll(winningDiagRIndicies)
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

            Log.d(LOG_TAG, "Winning indicies: $winningIndices")

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
        "It's ${whoseTurn.value.getUserName()}'s turn"
    else when (gameWinner) {
        null -> "The game was a draw \uD83E\uDD1D"
        else -> "\uD83C\uDFC6 ${
            gameWinner!!.getUserName()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } won the game \uD83C\uDFC6"
    }

    return Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                rootContainerWidthDp = pxToDp(
                    it.size.width,
                    context
                )

                rootContainerHeightDp = pxToDp(
                    it.size.height,
                    context
                )
            }
    ) {
        Column(
            modifier = Modifier
                .onGloballyPositioned {
                    menuSizeDp = pxToDp(
                        it.size.height,
                        context
                    )
                }
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically)
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
                            whoseTurn.value.getUsersCellState().getUserSymbol()
                        }s rain! \uD83D\uDCA7☔",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 10.dp, 0.dp, 30.dp),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ElevatedButton(
                    onClick = {
                        mapState.forEach {
                            it.fill(TTTCellState.EMPTY)
                        }

                        whoseTurn.value = UserTurn.USER_1
                        winningIndices = listOf()
                        gameState = TTTGameState.PLAYING
                    },
                    shape = CircleShape,
                    //colors = ButtonDefaults.buttonColors(ActiveCellBgColor),
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 30.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text("Replay \uD83D\uDD04")
                }

                ElevatedButton(
                    onClick = {
                        openMenuScreen()
                    },
                    shape = CircleShape,
                    //colors = ButtonDefaults.buttonColors(ActiveCellBgColor),
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 30.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text("Back to menu \uD83D\uDCCB")
                }
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
                            whoseTurn.value = when (whoseTurn.value) {
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
    TTTGameScreen(mapSize = 4, openMenuScreen = {})
}
