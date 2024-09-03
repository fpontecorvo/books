package com.library.books.book.domain.service

import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.BookRequirement
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class UniqueBookFilterProvider {
    suspend fun from(requirement: BookRequirement): BookFilter =
        with(requirement) {
            BookFilter(
                title = title,
                author = author,
            )
        }.log { info("unique book filter provided: {}", it) }

    suspend fun from(book: Book): BookFilter =
        with(book) {
            BookRequirement(
                title = title,
                author = author,
            ).let { from(it) }
        }

    companion object : CompanionLogger()
}
