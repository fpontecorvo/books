package com.library.books.shared.extensions

import com.library.books.shared.util.VT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun <T> suspendFlow(block: suspend () -> Flow<T>?): Flow<T> =
    flow {
        block()?.map { emit(it) }?.collect()
    }.flowOn(Dispatchers.VT)
