package com.brody.arxiv.core.common.collections

fun <T> equalsIgnoreOrderDirect(list1: Collection<T>?, list2: Collection<T>?) =
    list1?.size == list2?.size && list1?.toSet() == list2?.toSet()

fun <T> Collection<T>.equalsIgnoreOrder(list2: Collection<T>?) =
    equalsIgnoreOrderDirect(this, list2)

