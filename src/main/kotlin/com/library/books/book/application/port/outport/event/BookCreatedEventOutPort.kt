package com.library.books.book.application.port.outport.event

import arrow.core.Either
import com.library.books.book.domain.Book
import com.library.books.shared.domain.Error

fun interface BookCreatedEventOutPort {
    suspend fun produce(book: Book): Either<Error, Book>
}
