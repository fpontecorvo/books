package com.library.books.book.domain.service

import com.library.books.book.domain.Book
import com.library.books.book.domain.Book.StatusBreakDown
import com.library.books.book.domain.BookStatus.DELETED
import com.library.books.shared.extensions.AGT_ZONE_ID
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now

@Component
class DeletedBookProvider {
    suspend fun from(book: Book): Book =
        with(book) {
            deleteStatus(now(AGT_ZONE_ID)).let {
                copy(
                    status =
                        status.copy(
                            current = it,
                            history = status.history.plus(it),
                        ),
                )
            }
        }.log { info("deleted book provided: {}", it) }

    private fun deleteStatus(dateTime: OffsetDateTime) =
        StatusBreakDown(
            name = DELETED,
            date = dateTime,
        )

    companion object : CompanionLogger()
}
