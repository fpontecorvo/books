package com.library.books.book.application.service

import arrow.core.Either
import com.library.books.book.application.port.outport.repository.FindBookPageOutPort
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class FindBookPageService(
    private val repository: FindBookPageOutPort,
) {
    suspend fun findBy(
        filter: BookFilter,
        page: PageRequirement,
    ): Either<Error, Page<Book>> =
        repository.findBy(filter.excludeDeleted(), page)
            .logRight { info("book page found") }

    companion object : CompanionLogger()
}
