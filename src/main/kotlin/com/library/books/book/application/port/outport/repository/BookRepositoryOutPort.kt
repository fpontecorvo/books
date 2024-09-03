package com.library.books.book.application.port.outport.repository

import arrow.core.Either
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement
import kotlinx.coroutines.flow.Flow

internal interface BookRepositoryOutPort :
    SaveBookOutPort,
    FindBookByFilterOutPort,
    FindBookPageOutPort,
    FindBooksOutPort

fun interface SaveBookOutPort {
    suspend fun save(book: Book): Either<Error, Book>
}

fun interface FindBookByFilterOutPort {
    suspend fun findBy(filter: BookFilter): Either<Error, Book>
}

fun interface FindBookPageOutPort {
    suspend fun findBy(
        filter: BookFilter,
        page: PageRequirement,
    ): Either<Error, Page<Book>>
}

fun interface FindBooksOutPort {
    suspend fun find(): Either<Error, Flow<Book>>
}
