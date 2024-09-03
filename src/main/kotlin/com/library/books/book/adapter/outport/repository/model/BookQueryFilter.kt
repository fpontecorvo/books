package com.library.books.book.adapter.outport.repository.model

import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.BookStatus
import org.springframework.data.mongodb.core.query.Criteria.where
import java.util.UUID

data class BookQueryFilter(
    val id: UUID? = null,
    val title: String? = null,
    val author: String? = null,
    val status: BookStatus? = null,
    val exclude: Exclude? = null,
) {
    data class Exclude(
        val status: Set<BookStatus> = emptySet(),
    )

    fun fieldByCriteria() =
        mapOf(
            { where("_id").`is`(id) } to { id },
            { where("title").`is`(title) } to { title },
            { where("author").`is`(author) } to { author },
            { where("status").elemMatch(where("name").`is`(status!!.name)) } to { status },
            { where("status.name").nin(exclude!!.status) } to { exclude?.status },
        )

    companion object {
        fun from(filter: BookFilter) =
            with(filter) {
                BookQueryFilter(
                    id = id,
                    title = title,
                    author = author,
                    status = status,
                    exclude =
                        exclude?.let {
                            Exclude(
                                status = it.status,
                            )
                        },
                )
            }
    }
}
