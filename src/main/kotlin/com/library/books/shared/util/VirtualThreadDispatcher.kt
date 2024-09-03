package com.library.books.shared.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val Dispatchers.VT
    get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
