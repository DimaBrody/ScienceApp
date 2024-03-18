package com.brody.arxiv.core.common.properties

import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


inline fun <T> weak(
    referencedItem: T?,
    crossinline onConnected: (T?) -> Unit = {}
) =
    object : ReadWriteProperty<Any?, T?> {

        private var reference = WeakReference(referencedItem)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return reference.get()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            reference = WeakReference(value)
            onConnected(value)
        }
    }