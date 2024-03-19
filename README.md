# Tic-Tac-Toe Android App

A Jetpack Compose Android Tic-Tac-Toe game written in Kotlin.

## Table of contents

- [Tic-Tac-Toe Android App](#tic-tac-toe-android-app)
  - [Table of contents](#table-of-contents)
  - [Features](#features)
  - [Demo](#demo)
  - [APK Download](#apk-download)



## Features

- navigation with `jetpack-compose`
- arbitrary map size selection with validation
- haptic feedback (vibration)
- supports multiple orientations, preserving state
- winning sequence(s) detection and visualization

## Demo

| <div style="width:10vw">Map size validation</div> | <div style="width:10vw">Winning with a single pattern</div> | <div style="width:10vw">Winning with multiple patterns</div> | <div style="width:10vw">Replay button</div> | <div style="width:10vw">Multiple screen orientations support</div> |
| ------------------------------------------------- | ----------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------- | ------------------------------------------------------------------ |
| <img src="./images/mapsize_validation.gif">       | <img src="./images/winning_map_3x3.gif">                    | <img src="./images/multiple_wins_map_5x5.gif">               | <img src="./images/replay_and_a_draw.gif">  | <img src="./images/orientation_change.gif">                        |

## APK Download

The newest APK release is available for [download here](https://github.com/Xintre/TicTacToe/releases).

It is automatically built with GH Actions [Continous Delivery workflow](./.github/workflows/cd.yml).
