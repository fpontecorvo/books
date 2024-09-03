package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.aCreatedBook
import com.library.books.aDeletedBook
import com.library.books.book.application.port.outport.event.BookDeletedEventOutPort
import com.library.books.book.application.service.SaveCachedBookService
import com.library.books.book.domain.service.DeletedBookProvider
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class DeleteBookSpec : FeatureSpec({

    val deletedBook = mockk<DeletedBookProvider>()
    val cache = mockk<SaveCachedBookService>()
    val event = mockk<BookDeletedEventOutPort>()

    val deleteBook = DeleteBook(deletedBook, cache, event)

    beforeEach { clearAllMocks() }

    feature("delete book") {
        val aCreatedBook = aCreatedBook()
        val aDeletedBook = aDeletedBook()

        scenario("successfully delete") {
            coEvery { deletedBook.from(aCreatedBook) } returns aDeletedBook
            coEvery { event.produce(aDeletedBook) } returns aDeletedBook.right()
            coEvery { cache.save(aDeletedBook) } returns aDeletedBook.right()

            deleteBook.execute(aCreatedBook) shouldBeRight aDeletedBook

            coVerify(exactly = 1) { deletedBook.from(aCreatedBook) }
            coVerify(exactly = 1) { event.produce(aDeletedBook) }
            coVerify(exactly = 1) { cache.save(aDeletedBook) }
        }

        scenario("fail to produce delete") {
            val error = Error(message = "error", type = Business)

            coEvery { deletedBook.from(aCreatedBook) } returns aDeletedBook
            coEvery { event.produce(aDeletedBook) } returns error.left()

            deleteBook.execute(aCreatedBook) shouldBeLeft error

            coVerify(exactly = 1) { deletedBook.from(aCreatedBook) }
            coVerify(exactly = 1) { event.produce(aDeletedBook) }
            coVerify(exactly = 0) { cache.save(aDeletedBook) }
        }
    }
})
