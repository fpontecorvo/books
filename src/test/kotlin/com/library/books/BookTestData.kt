package com.library.books

import com.library.books.book.domain.Book
import com.library.books.book.domain.Book.StatusBreakDown
import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.BookRequirement
import com.library.books.book.domain.BookStatus.CREATED
import com.library.books.book.domain.BookStatus.DELETED
import com.library.books.book.domain.BookStatus.UPDATED
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import com.library.books.shared.domain.Error.Type.MissingResource
import com.library.books.shared.domain.Error.Type.Server
import com.library.books.shared.extensions.AGT_ZONE_OFFSET
import java.time.OffsetDateTime
import java.util.UUID

val id: UUID = UUID.fromString("34cbfdaf-e2a5-4c51-9478-02d64b358019")
const val TITLE = "The Lord of the Rings"
const val AUTHOR = "J.R.R. Tolkien"
const val UPDATED_TITLE = "The Fellowship of the Ring"
const val UPDATED_AUTHOR = "Tolkien, J.R.R."
val createdDate: OffsetDateTime = OffsetDateTime.of(2023, 5, 12, 15, 30, 12, 0, AGT_ZONE_OFFSET)
val updatedDate: OffsetDateTime = OffsetDateTime.of(2023, 5, 24, 13, 5, 42, 0, AGT_ZONE_OFFSET)
val deletedDate: OffsetDateTime = OffsetDateTime.of(2023, 6, 4, 12, 23, 5, 0, AGT_ZONE_OFFSET)

fun aBookRequirement(
    newTitle: String = TITLE,
    newAuthor: String = AUTHOR,
) = BookRequirement(
    title = newTitle,
    author = newAuthor,
)

fun anUpdateBookRequirement() =
    BookRequirement(
        title = UPDATED_TITLE,
        author = UPDATED_AUTHOR,
    )

fun aCreatedBook() =
    Book(
        id = id,
        title = TITLE,
        author = AUTHOR,
        status =
            Book.Status(
                current =
                    StatusBreakDown(
                        name = CREATED,
                        date = createdDate,
                    ),
                history =
                    listOf(
                        StatusBreakDown(
                            name = CREATED,
                            date = createdDate,
                        ),
                    ),
            ),
    )

fun anUpdatedBook() =
    Book(
        id = id,
        title = UPDATED_TITLE,
        author = UPDATED_AUTHOR,
        status =
            Book.Status(
                current =
                    StatusBreakDown(
                        name = UPDATED,
                        date = updatedDate,
                    ),
                history =
                    listOf(
                        StatusBreakDown(
                            name = CREATED,
                            date = createdDate,
                        ),
                        StatusBreakDown(
                            name = UPDATED,
                            date = updatedDate,
                        ),
                    ),
            ),
    )

fun aDeletedBook() =
    aCreatedBook().let {
        it.copy(
            status =
                Book.Status(
                    current = StatusBreakDown(name = DELETED, date = deletedDate),
                    history = it.status.history.plus(StatusBreakDown(name = DELETED, date = deletedDate)),
                ),
        )
    }

fun aGenericServerError(throwable: Throwable? = null) = Error(message = "test server error", type = Server, cause = throwable)

fun aGenericBusinessError() = Error(message = "test business error", type = Business)

fun aGenericMissingResource() = Error(message = "test missing resource", type = MissingResource)

fun aBookFilter() =
    BookFilter(
        id = id,
        title = TITLE,
        author = AUTHOR,
        status = null,
        exclude = null,
    )
