package com.library.books.book.adapter.inport.controller.model

import com.library.books.book.domain.Book
import com.library.books.book.domain.BookStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.util.UUID

@Schema(name = "BookResponse")
data class BookResponse(
    @Schema(
        type = "string",
        format = "uuid",
        example = "9374801c-ff95-4dbf-8434-d40300a8fb97",
        description = "books identifier",
    )
    val id: UUID,
    @Schema(
        type = "string",
        example = "A wizard of Earthsea",
        description = "main title of the book",
    )
    val title: String,
    @Schema(
        type = "string",
        example = "Ursula K. Le Guin",
        description = "full name of the author",
    )
    val author: String,
    @Schema(
        name = "status",
        type = "array",
        description = "status the book went through",
    )
    val status: Status,
    val thread: String,
) {
    @Schema(name = "BookResponse.Status")
    data class Status(
        @Schema(
            type = "enum",
            description = "status of the book",
            example = "CREATED",
        )
        val name: BookStatus,
        @Schema(
            type = "date",
            example = "2022-12-14T04:51:47",
            description = "date in which book achieve this status",
        )
        val date: OffsetDateTime,
    )

    companion object {
        fun from(book: Book) =
            with(book) {
                BookResponse(
                    id = id,
                    title = title,
                    author = author,
                    status =
                        Status(
                            name = status.current.name,
                            date = status.current.date,
                        ),
                    thread = Thread.currentThread().toString(),
                )
            }
    }
}
