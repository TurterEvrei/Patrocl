package org.turter.patrocl.utils

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.toFormattedString(digits: Int): String {
    val factor = 10.0.pow(digits)
    return ((this * factor).roundToInt() / factor).toString()
}