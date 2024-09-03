package com.library.books.shared.domain

data class Page<T>(
    val content: List<T>,
    val metadata: Metadata,
) {
    data class Metadata(
        val size: Int,
        val totalElements: Long,
        val totalPages: Int,
        val number: Int,
    )
}
