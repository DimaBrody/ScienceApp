package com.brody.arxiv.core.common.extensions

inline fun <T, R : Any> Iterable<T>?.mapNotNullEmpty(transform: (T) -> R?): List<R> {
    return this?.mapNotNullTo(ArrayList<R>(), transform) ?: emptyList()
}