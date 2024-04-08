package com.brody.arxiv.core.data.proto

import com.google.protobuf.FloatValue
import com.google.protobuf.Int32Value

fun FloatValue.toFloat(): Float? =
    if (this.isInitialized) this.value else null

fun Int32Value.toInt(): Int? =
    if (this.isInitialized) this.value else null