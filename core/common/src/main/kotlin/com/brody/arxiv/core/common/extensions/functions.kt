package com.brody.arxiv.core.common.extensions

inline fun <T, R : Any> Iterable<T>?.mapNotNullEmpty(transform: (T) -> R?): List<R> {
    return this?.mapNotNullTo(ArrayList<R>(), transform) ?: emptyList()
}

fun Boolean.toInt() = if (this) 1 else 0

fun Boolean.toFloat() = if (this) 1f else 0f