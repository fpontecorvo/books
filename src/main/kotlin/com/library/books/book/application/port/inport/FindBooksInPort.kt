package com.library.books.book.application.port.inport

import arrow.core.Either
import com.library.books.book.domain.Book
import com.library.books.shared.domain.Error
import kotlinx.coroutines.flow.Flow

fun interface FindBooksInPort {
    suspend fun execute(): Either<Error, Flow<Book>>
}
