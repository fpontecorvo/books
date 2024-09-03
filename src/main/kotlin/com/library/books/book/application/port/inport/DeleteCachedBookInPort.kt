package com.library.books.book.application.port.inport

import arrow.core.Either
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error

fun interface DeleteCachedBookInPort {
    suspend fun execute(identifier: BookFilter): Either<Error, Unit>
}
