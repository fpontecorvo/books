package com.library.books.book.application.port.inport

import arrow.core.Either
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement

fun interface FindBookPageInPort {
    suspend fun execute(
        filter: BookFilter,
        page: PageRequirement,
    ): Either<Error, Page<Book>>
}
