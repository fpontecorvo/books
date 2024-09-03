package com.library.books.book.application.port.inport

import arrow.core.Either
import com.library.books.book.domain.Book
import com.library.books.shared.domain.Error
import java.util.UUID

fun interface FindBookInPort {
    suspend fun execute(id: UUID): Either<Error, Book>
}
