package com.library.books.book.application.port.inport

import arrow.core.Either
import com.library.books.shared.domain.Error

fun interface DeleteAllCachedBookInPort {
    suspend fun execute(): Either<Error, Unit>
}
