package com.library.books.shared.adapter.inport.controller.model

import com.library.books.shared.domain.Page

data class PageResponse<T>(
    val content: List<T>,
    val metadata: Metadata,
) {
    data class Metadata(
        val size: Int,
        val totalElements: Long,
        val totalPages: Int,
        val number: Int,
    )

    companion object {
        fun <T, R> from(
            page: Page<R>,
            mapContent: (R) -> T,
        ) = with(page) {
            PageResponse(
                content = content.toList().map(mapContent),
                metadata =
                    with(metadata) {
                        Metadata(
                            size = size,
                            totalElements = totalElements,
                            totalPages = totalPages,
                            number = number,
                        )
                    },
            )
        }
    }
}
