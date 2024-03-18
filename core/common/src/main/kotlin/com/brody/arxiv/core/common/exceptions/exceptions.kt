package com.brody.arxiv.core.common.exceptions

import java.io.IOException

class GenericNetworkException(
    message: String? = "An error occurred during the network call",
    cause: Throwable? = null
) : IOException(message, cause)

class OfflineException(message: String? = "No network available") : IOException(message)

class NoSavedException(message: String? = "You have no saved items") : IOException(message)

