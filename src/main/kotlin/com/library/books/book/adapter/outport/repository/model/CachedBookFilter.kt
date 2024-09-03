package com.library.books.book.adapter.outport.repository.model

import com.library.books.book.domain.BookFilter
import java.util.UUID

data class CachedBookFilter(
    val id: UUID? = null,
    val title: String? = null,
    val author: String? = null,
) {
    companion object {
        fun from(filter: BookFilter) =
            with(filter) {
                CachedBookFilter(
                    id = id,
                    title = title,
                    author = author,
                )
            }
    }

    fun key() = "books${id.keyShaped()}${author.keyShaped()}${title.keyShaped()}"

    private fun <A> A?.keyShaped() =
        (if (this == null) "" else ":$this")
            .replace(" ", "_")
}
