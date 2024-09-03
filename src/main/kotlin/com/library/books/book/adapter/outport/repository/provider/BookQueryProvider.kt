package com.library.books.book.adapter.outport.repository.provider

import com.library.books.book.adapter.outport.repository.model.BookQueryFilter
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class BookQueryProvider {
    suspend fun from(filter: BookQueryFilter) =
        Query().also { query ->
            filter.toCriteria().map {
                query.addCriteria(it)
            }
        }

    private fun BookQueryFilter.toCriteria() =
        fieldByCriteria().mapNotNull { fieldByCriteria ->
            with(fieldByCriteria) {
                value.invoke()?.let { key.invoke() }
            }
        }
}
