package com.library.books.book.adapter.inport.controller.model

import com.library.books.book.domain.BookRequirement
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(name = "BookRequest")
data class BookRequest(
    @field:Size(max = 144, message = "title field cant be larger than 144 characters")
    @field:Size(min = 1, message = "title field must have at least one character")
    val title: String,
    @Schema(
        type = "string",
        example = "Ursula K. Le Guin",
        description = "full name of the author",
    )
    @field:Size(max = 144, message = "author field cant be larger than 144 characters")
    @field:Size(min = 1, message = "author field must have at least one character")
    val author: String,
) {
    fun toRequirement() =
        BookRequirement(
            title = title,
            author = author,
        )
}
