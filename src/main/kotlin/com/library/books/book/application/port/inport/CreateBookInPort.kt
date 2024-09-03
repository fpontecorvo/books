package com.library.books.book.application.port.inport

import arrow.core.Either
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookRequirement
import com.library.books.shared.domain.Error

fun interface CreateBookInPort {
    suspend fun execute(requirement: BookRequirement): Either<Error, Book>
}
