package com.library.books.book.adapter.inport.controller.model

import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.BookStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

data class BookFilterParams(
    @Schema(
        type = "string",
        format = "uuid",
        example = "9374801c-ff95-4dbf-8434-d40300a8fb97",
        description = "books identifier",
    )
    val id: UUID? = null,
    @Schema(
        type = "string",
        example = "A wizard of Earthsea",
        description = "main title of the book",
        required = false,
    )
    val title: String? = null,
    @Schema(
        type = "string",
        example = "Ursula K. Le Guin",
        description = "full name of the author",
        required = false,
    )
    val author: String? = null,
    @Schema(
        type = "enum",
        description = "status of the book",
        example = "CREATED",
        required = false,
    )
    val status: BookStatus? = null,
) {
    fun toFilter() =
        BookFilter(
            id = id,
            title = title,
            author = author,
            status = status,
        )
}
