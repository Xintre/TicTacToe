package com.xintre.tictactoe.ui.screens

import androidx.compose.runtime.MutableState

/**
 * Validation result for map size string
 *
 * @param errorToShowOnUI `null` if the error is not to be shown or `String` to be displayed on the UI, describing the error otherwise
 * @param isTrulyValid `Boolean` indicating whether the input string is truly valid and can be used as an `Int`
 */
class MapSizeValidationResult private constructor(
    val errorToShowOnUI: String?,
    val isTrulyValid: Boolean
) {
    fun shouldShowErrorOnUI(): Boolean {
        return errorToShowOnUI != null
    }

    companion object {
        /**
         * This function validates whether [mapSizeStr] is a valid map size integer.
         *
         * @param mapSizeStr The map size string to be validated.
         * @return validation result
         */
        fun validateMapSize(mapSizeStr: MutableState<String>): MapSizeValidationResult {
            // edge case: when string empty, don't show the error
            if (mapSizeStr.value.isEmpty()) {
                return MapSizeValidationResult(null, false)
            }

            return try {
                val mapSizeInt = Integer.parseInt(mapSizeStr.value)

                if (mapSizeInt < 2) {
                    MapSizeValidationResult("Map size must be > 1", false)
                } else {
                    MapSizeValidationResult(null, true)
                }
            } catch (error: NumberFormatException) {
                MapSizeValidationResult("Invalid number", false)
            }
        }
    }
}
