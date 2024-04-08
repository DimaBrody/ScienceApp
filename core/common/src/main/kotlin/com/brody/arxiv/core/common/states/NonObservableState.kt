package com.brody.arxiv.core.common.states

class NonObservableState<T>(initialValue: T) {
    var value: T = initialValue
        private set

    fun update(newValue: T) {
        value = newValue
    }
}