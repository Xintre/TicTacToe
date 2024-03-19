package com.xintre.tictactoe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xintre.tictactoe.ui.screens.MapSizeValidationResult.Companion.validateMapSize
import com.xintre.tictactoe.ui.theme.ActiveCellBgColor
import com.xintre.tictactoe.ui.theme.InactiveCellBgColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    openGameScreen: (desiredMapSize: Int) -> Unit
) {
    val mapSizeStr = remember { mutableStateOf("") }
    val mapSizeStrValidationResult by remember { derivedStateOf { validateMapSize(mapSizeStr) } }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {}
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enter the size of the map! \uD83C\uDFB2",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                // to pass via navigation we need to have INT!
                value = mapSizeStr.value,
                onValueChange = {
                    mapSizeStr.value = it
                },
                label = { Text("mapSize") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ActiveCellBgColor,
                    unfocusedBorderColor = InactiveCellBgColor,
                ),
                isError = mapSizeStrValidationResult.shouldShowErrorOnUI(),
                supportingText = {
                    if (mapSizeStrValidationResult.shouldShowErrorOnUI()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = mapSizeStrValidationResult.errorToShowOnUI!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    if (mapSizeStrValidationResult.shouldShowErrorOnUI())
                        Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                },
                keyboardActions = KeyboardActions { validateMapSize(mapSizeStr) },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(
                onClick = {
                    if (mapSizeStrValidationResult.isTrulyValid) {
                        openGameScreen(mapSizeStrValidationResult.mapSizeInt!!)
                    }
                },
                shape = CircleShape,
                //colors = ButtonDefaults.buttonColors(ActiveCellBgColor),
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 30.dp)
                    .align(Alignment.CenterVertically),
                enabled = !(mapSizeStrValidationResult.shouldShowErrorOnUI())
            ) {
                Text(
                    "PLAY ▶\uFE0F",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {}
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            ElevatedButton(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(10.dp),
                //colors = ButtonDefaults.buttonColors(ActiveCellBgColor),
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 30.dp)
                    .align(Alignment.Bottom),
            ) {
                Text(
                    "Show the stats \uD83D\uDCF6",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }

}


@Preview
@Composable
fun MenuScreenPreview() {
    MenuScreen {}
}
