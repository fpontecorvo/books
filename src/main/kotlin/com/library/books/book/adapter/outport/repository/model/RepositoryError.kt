package com.library.books.book.adapter.outport.repository.model

import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.MissingResource
import com.library.books.shared.domain.Error.Type.Server

fun notFound(key: String) = Error(message = "book not found in cache - key: $key", type = MissingResource)

fun notFound(filter: BookFilter) = Error(message = "book not found: $filter", type = MissingResource)

fun cacheError(throwable: Throwable) = Error(message = "error communicating with cache", type = Server, cause = throwable)

fun dbError(throwable: Throwable) = Error(message = "error communicating with db", type = Server, cause = throwable)
