package com.library.books.book.domain.service

import com.library.books.book.domain.Book
import com.library.books.book.domain.Book.StatusBreakDown
import com.library.books.book.domain.BookRequirement
import com.library.books.book.domain.BookStatus.CREATED
import com.library.books.shared.extensions.AGT_ZONE_ID
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import java.util.UUID.randomUUID

@Component
class BookProvider {
    suspend fun from(requirement: BookRequirement): Book =
        with(requirement) {
            Book(
                id = randomUUID(),
                title = title,
                author = author,
                status =
                    createStatus(now(AGT_ZONE_ID)).let {
                        Book.Status(
                            current = it,
                            history = listOf(it),
                        )
                    },
            )
        }.log { info("book provided: {}", it) }

    private fun createStatus(datetime: OffsetDateTime) =
        StatusBreakDown(
            name = CREATED,
            date = datetime,
        )

    companion object : CompanionLogger()
}
