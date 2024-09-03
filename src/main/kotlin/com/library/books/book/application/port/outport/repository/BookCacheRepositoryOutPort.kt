package com.library.books.book.application.port.outport.repository

import arrow.core.Either
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error

internal interface BookCacheRepositoryOutPort :
    SaveCachedBookOutPort,
    FindCachedBookByFilterOutPort,
    DeleteCachedBookOutPort,
    DeleteAllCachedBookOutPort

fun interface FindCachedBookByFilterOutPort {
    suspend fun findBy(identifier: BookFilter): Either<Error, Book>
}

fun interface SaveCachedBookOutPort {
    suspend fun save(
        book: Book,
        identifier: BookFilter,
    ): Either<Error, Book>
}

fun interface DeleteCachedBookOutPort {
    suspend fun delete(identifier: BookFilter): Either<Error, Unit>
}

fun interface DeleteAllCachedBookOutPort {
    suspend fun deleteAll(): Either<Error, Unit>
}
