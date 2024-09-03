package com.library.books.book.application.usecase

import arrow.core.Either
import com.library.books.book.application.port.inport.FindBookPageInPort
import com.library.books.book.application.service.FindBookPageService
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindBookPage(
    private val find: FindBookPageService,
) : FindBookPageInPort {
    override suspend fun execute(
        filter: BookFilter,
        page: PageRequirement,
    ): Either<Error, Page<Book>> =
        log(filter, page) {
            find.findBy(filter, page)
        }

    private suspend fun log(
        filter: BookFilter,
        page: PageRequirement,
        action: suspend () -> Either<Error, Page<Book>>,
    ) = log.info("find book page requirement received: {} - {}", filter, page)
        .run { action() }
        .logEither(
            { error("book page not found: {}", it.message) },
            { info("book page found: {}", it) },
        )

    companion object : CompanionLogger()
}
