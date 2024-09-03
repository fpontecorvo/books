package com.library.books.book.application.service

import arrow.core.Either
import arrow.core.raise.either
import com.library.books.book.application.port.outport.repository.SaveCachedBookOutPort
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.service.UniqueBookFilterProvider
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class SaveCachedBookService(
    private val uniqueFilter: UniqueBookFilterProvider,
    private val cache: SaveCachedBookOutPort,
) {
    suspend fun save(book: Book): Either<Error, Book> =
        with(book) {
            either {
                saveBy(id()).bind()
                saveBy(uniqueness()).bind()
            }
        }.logRight { info("cached book saved: {}", it.id) }

    private suspend fun Book.saveBy(identifier: BookFilter) = cache.save(this, identifier)

    private fun Book.id() = BookFilter(id = id)

    private suspend fun Book.uniqueness() = uniqueFilter.from(this)

    companion object : CompanionLogger()
}
