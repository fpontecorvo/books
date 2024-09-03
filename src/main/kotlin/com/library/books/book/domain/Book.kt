package com.library.books.book.domain

import com.library.books.book.domain.BookStatus.DELETED
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.UUID

@Document
data class Book(
    val id: UUID,
    val title: String,
    val author: String,
    val status: Status,
) {
    data class Status(
        val current: StatusBreakDown,
        val history: List<StatusBreakDown>,
    )

    data class StatusBreakDown(
        val name: BookStatus,
        val date: OffsetDateTime,
    )

    fun deleted() = status.current.name == DELETED
}
