package com.brody.arxiv.core.network.util

import kotlinx.coroutines.flow.Flow
import java.io.IOException

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}


