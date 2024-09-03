package com.library.books.adapter.inport.controller

import com.library.books.AUTHOR
import com.library.books.TITLE
import com.library.books.UPDATED_AUTHOR
import com.library.books.UPDATED_TITLE
import com.library.books.book.adapter.inport.controller.model.BookRequest
import com.library.books.book.adapter.inport.controller.model.BookResponse
import com.library.books.book.adapter.inport.controller.model.BookResponse.Status
import com.library.books.book.domain.BookStatus.CREATED
import com.library.books.book.domain.BookStatus.DELETED
import com.library.books.book.domain.BookStatus.UPDATED
import com.library.books.createdDate
import com.library.books.deletedDate
import com.library.books.id
import com.library.books.updatedDate

private const val THREAD = "VirtualThread[#120]/runnable@ForkJoinPool-1-worker-7"

fun aBookRequest() =
    BookRequest(
        title = TITLE,
        author = AUTHOR,
    )

fun anUpdateBookRequest() =
    BookRequest(
        title = UPDATED_TITLE,
        author = UPDATED_AUTHOR,
    )

fun aCreatedBookResponse() =
    BookResponse(
        id = id,
        title = TITLE,
        author = AUTHOR,
        status =
            Status(name = CREATED, date = createdDate),
        thread = THREAD,
    )

fun anUpdatedBookResponse() =
    BookResponse(
        id = id,
        title = UPDATED_TITLE,
        author = UPDATED_AUTHOR,
        status = Status(name = UPDATED, date = updatedDate),
        thread = THREAD,
    )

fun aDeletedBookResponse() = aCreatedBookResponse().copy(status = Status(name = DELETED, date = deletedDate))
