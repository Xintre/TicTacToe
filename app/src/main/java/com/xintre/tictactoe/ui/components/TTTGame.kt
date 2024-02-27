package com.xintre.tictactoe.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.xintre.tictactoe.gameLogic.MapState
import com.xintre.tictactoe.gameLogic.TTTCell
import com.xintre.tictactoe.gameLogic.TTTCellState
import com.xintre.tictactoe.ui.Constants

fun <T> prepareTTTGrid(mapSize: Int, renderCell: (row: Int, col: Int) -> T): List<List<T>> {
    return (0..<mapSize).map { row ->
        (0..<mapSize).map { col ->
            renderCell(row, col)
        }
    }
}

@Composable
fun TTTGame(mapSize: Int) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val screenRowSize = configuration.screenWidthDp
//        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
//            configuration.screenWidthDp
//        else
//            configuration.screenHeightDp

    val cellSizeDp = screenRowSize / mapSize - Constants.TTT_CELL_MARGIN_DP * 2

    val mapState = remember {
        mutableStateOf<MapState>(
            prepareTTTGrid(mapSize) { _, _ ->
                TTTCellState.EMPTY
            }
        )
    }

    return Column(
        verticalArrangement = Arrangement.Center,
    ) {
        prepareTTTGrid(mapSize) { row, col ->
            Pair(row, col)
        }.forEachIndexed { row, rowList ->
            Row(
                modifier = with(Modifier) {
                    fillMaxWidth()
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                rowList.forEachIndexed { col, _ ->
                    TTTCell(
                        state = mapState.value[row][col],
                        cellSizeDp = cellSizeDp
                    ) {
                        Toast.makeText(
                            context,
                            "Cell R: $row, C: $col has been clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TTTGamePreview() {
    TTTGame(mapSize = 4)
}
