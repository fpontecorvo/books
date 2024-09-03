package com.library.books.book.domain

import com.library.books.book.domain.BookStatus.DELETED
import java.util.UUID

data class BookFilter(
    val id: UUID? = null,
    val title: String? = null,
    val author: String? = null,
    val status: BookStatus? = null,
    val exclude: Exclude? = null,
) {
    data class Exclude(
        val status: Set<BookStatus> = emptySet(),
    )

    fun excludeDeleted() = copy(exclude = exclude?.copy(status = exclude.status.plus(DELETED)) ?: Exclude(status = setOf(DELETED)))
}
