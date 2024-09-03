package com.library.books.book.application.usecase

import arrow.core.Either
import com.library.books.book.application.port.inport.FindBookInPort
import com.library.books.book.application.service.FindBookService
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindBook(
    private val service: FindBookService,
) : FindBookInPort {
    override suspend fun execute(id: UUID): Either<Error, Book> =
        id.log {
            service.findBy(id.bookFilter())
        }

    private fun UUID.bookFilter() = BookFilter(id = this)

    private suspend fun UUID.log(action: suspend UUID.() -> Either<Error, Book>) =
        logged(
            description = "find book  by id",
            leftLog = "book not found",
            rightLog = "book found",
        ) { action.invoke(this) }

    companion object : CompanionLogger()
}
