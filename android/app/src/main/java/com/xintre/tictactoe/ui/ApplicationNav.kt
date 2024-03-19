package com.xintre.tictactoe.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.xintre.tictactoe.ui.screens.MenuScreen
import com.xintre.tictactoe.ui.screens.TTTGameScreen

@Composable
fun ApplicationNav() {
    var mapSize by rememberSaveable { mutableStateOf(3) }
    val navController = rememberNavController()
    val navGraph = remember(navController) {
        navController.createGraph(startDestination = "menuscreen") {
            composable("menuscreen") {
                MenuScreen(
                    openGameScreen = { desiredMapSize ->
                        mapSize = desiredMapSize
                        navController.navigate("tttgamescreen")
                    }
                )
            }
            composable("tttgamescreen") {
                TTTGameScreen(
                    mapSize = mapSize,
                    openMenuScreen = {
                        navController.navigate("menuscreen")
                    }
                )
            }
//            composable("tttgamestats") { FriendsList(mapSize: Int) }
        }
    }

    NavHost(navController, navGraph)
}