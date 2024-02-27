package com.xintre.tictactoe.gameLogic

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xintre.tictactoe.ui.Constants

@Composable
fun TTTCell(state: TTTCellState, cellSizeDp: Int, onClick: () -> Unit) {
    return Box(modifier = Modifier.padding(Constants.TTT_CELL_MARGIN_DP.dp)) {
        Box(
            modifier = with(Modifier) {
                size(cellSizeDp.dp)
                    .background(Color(0xD966CEFD), shape = RoundedCornerShape(16.dp))
            }) {
            TextButton(
                shape = MaterialTheme.shapes.large,
                onClick = onClick,
                modifier = Modifier.align(alignment = Alignment.Center)
            ) {
                Text(
                    when (state) {
                        TTTCellState.EMPTY -> {
                            " "
                        }

                        TTTCellState.CROSS -> {
                            "❌"
                        }

                        TTTCellState.CIRCLE -> {
                            "⭕"
                        }
                    },
                    fontSize = 30.sp,
                )
            }
        }
    }
}

@Preview
@Composable
fun TTTCellPreview() {
    return TTTCell(TTTCellState.CROSS, 60) {}
}
