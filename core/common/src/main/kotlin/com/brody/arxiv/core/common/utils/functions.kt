package com.brody.arxiv.core.common.utils

fun createIdFromString(input: String, onNull: String): Int {
    val digitsOnly = input.filter { it.isDigit() }
    return digitsOnly.toIntOrNull() ?: onNull.length
}

fun Float.round(decimals: Int = 1): String =
    if (this % 1 == 0.0f) this.toInt().toString() else "%.${decimals}f".format(this)

val String.sizeInBytes: Float
    get() = this.length * 2f
