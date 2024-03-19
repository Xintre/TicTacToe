package com.xintre.tictactoe.ui

import android.content.Context
import android.util.TypedValue

// inspired by: https://stackoverflow.com/a/29665208/23509903

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}

fun dpToSp(dp: Float, context: Context): Float {
    return (dpToPx(dp, context) / context.resources.displayMetrics.scaledDensity)
}

fun pxToDp(px: Int, context: Context): Float {
    return px / context.resources.displayMetrics.density
}
