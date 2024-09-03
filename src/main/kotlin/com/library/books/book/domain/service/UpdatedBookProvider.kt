package com.library.books.book.domain.service

import com.library.books.book.domain.Book
import com.library.books.book.domain.Book.StatusBreakDown
import com.library.books.book.domain.BookRequirement
import com.library.books.book.domain.BookStatus.UPDATED
import com.library.books.shared.extensions.AGT_ZONE_ID
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now

@Component
class UpdatedBookProvider {
    suspend fun from(
        requirement: BookRequirement,
        book: Book,
    ): Book =
        with(requirement) {
            book.copy(
                title = title,
                author = author,
                status =
                    updateStatus(now(AGT_ZONE_ID)).let {
                        book.status.copy(
                            current = it,
                            history = book.status.history.plus(it),
                        )
                    },
            )
        }.log { info("updated book provided: {}", it) }

    private fun updateStatus(dateTime: OffsetDateTime) =
        StatusBreakDown(
            name = UPDATED,
            date = dateTime,
        )

    companion object : CompanionLogger()
}
