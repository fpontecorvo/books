package com.library.books.book.application.service

import arrow.core.Either
import com.library.books.book.adapter.outport.repository.model.notFound
import com.library.books.book.application.port.outport.repository.FindBookByFilterOutPort
import com.library.books.book.application.port.outport.repository.FindCachedBookByFilterOutPort
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import com.library.books.shared.util.flatMapLeft
import com.library.books.shared.util.leftIf
import org.springframework.stereotype.Component

@Component
class FindBookService(
    private val cache: FindCachedBookByFilterOutPort,
    private val repository: FindBookByFilterOutPort,
) {
    suspend fun findBy(filter: BookFilter): Either<Error, Book> =
        with(filter) {
            findCached()
                .flatMapLeft { find() }
                .leftIf(notFound(this)) { deleted() }
        }
            .logEither(
                { error("error searching for book: {}", it.message) },
                { info("book found: {}", it.id) },
            )

    private suspend fun BookFilter.findCached() = cache.findBy(this)

    private suspend fun BookFilter.find() = repository.findBy(this)

    companion object : CompanionLogger()
}
