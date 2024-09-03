package com.library.books.shared.adapter.out.repository.model.mapper

import com.library.books.shared.domain.Page
import org.springframework.stereotype.Component
import org.springframework.data.domain.Page as DocumentPage

@Component
class FromDocumentPageToPageMapper {
    suspend fun <T, R> from(
        page: DocumentPage<R>,
        mapContent: (R) -> T,
    ): Page<T> =
        with(page) {
            Page(
                content = content.toList().map(mapContent),
                metadata =
                    Page.Metadata(
                        size = size,
                        totalElements = totalElements,
                        totalPages = totalPages,
                        number = number,
                    ),
            )
        }
}
