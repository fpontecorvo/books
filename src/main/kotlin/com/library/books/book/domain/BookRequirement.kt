package com.library.books.book.domain

data class BookRequirement(
    val title: String,
    val author: String,
) {
    companion object {
        fun from(book: Book) =
            with(book) {
                BookRequirement(
                    title = title,
                    author = author,
                )
            }
    }
}
