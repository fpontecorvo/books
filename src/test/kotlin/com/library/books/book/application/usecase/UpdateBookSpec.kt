package com.library.books.book.application.usecase

import arrow.core.right
import com.library.books.aBookRequirement
import com.library.books.aCreatedBook
import com.library.books.anUpdatedBook
import com.library.books.book.application.port.outport.event.BookCreatedEventOutPort
import com.library.books.book.application.service.SaveCachedBookService
import com.library.books.book.domain.service.UpdatedBookProvider
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class UpdateBookSpec : FeatureSpec({

    val updatedBook = mockk<UpdatedBookProvider>()
    val event = mockk<BookCreatedEventOutPort>()
    val cache = mockk<SaveCachedBookService>()

    val updateBook = UpdateBook(updatedBook, event, cache)

    beforeEach { clearAllMocks() }

    feature("update book") {
        val book = aCreatedBook()
        val anUpdatedBook = anUpdatedBook()
        scenario("successfully update book") {
            val requirement = aBookRequirement("new Title", "new Author")

            coEvery { updatedBook.from(requirement, book) } returns anUpdatedBook
            coEvery { event.produce(anUpdatedBook) } returns anUpdatedBook.right()
            coEvery { cache.save(anUpdatedBook) } returns anUpdatedBook.right()

            updateBook.execute(requirement, book) shouldBeRight anUpdatedBook

            coVerify(exactly = 1) { updatedBook.from(requirement, book) }
            coVerify(exactly = 1) { event.produce(anUpdatedBook) }
            coVerify(exactly = 1) { cache.save(anUpdatedBook) }
        }

        scenario("nothing to update") {
            val requirement = aBookRequirement()

            updateBook.execute(requirement, book) shouldBeRight book

            coVerify(exactly = 0) { updatedBook.from(requirement, book) }
            coVerify(exactly = 0) { event.produce(anUpdatedBook) }
            coVerify(exactly = 0) { cache.save(anUpdatedBook) }
        }
    }
})
