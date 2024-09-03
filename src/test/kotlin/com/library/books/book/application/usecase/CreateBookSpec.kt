package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.aBookRequirement
import com.library.books.aCreatedBook
import com.library.books.book.application.port.outport.event.BookCreatedEventOutPort
import com.library.books.book.application.service.BookUniquenessValidator
import com.library.books.book.application.service.SaveCachedBookService
import com.library.books.book.domain.service.BookProvider
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class CreateBookSpec : FeatureSpec({

    val uniqueness = mockk<BookUniquenessValidator>()
    val newBook = mockk<BookProvider>()
    val cache = mockk<SaveCachedBookService>()
    val event = mockk<BookCreatedEventOutPort>()

    val createBook = CreateBook(uniqueness, newBook, cache, event)

    beforeEach { clearAllMocks() }

    feature("create a book") {
        val requirement = aBookRequirement()
        val book = aCreatedBook()

        scenario("successfully creation book") {

            coEvery { uniqueness.validate(requirement) } returns requirement.right()
            coEvery { newBook.from(requirement) } returns book
            coEvery { event.produce(book) } returns book.right()
            coEvery { cache.save(book) } returns book.right()

            createBook.execute(requirement) shouldBeRight book

            coVerify(exactly = 1) { uniqueness.validate(requirement) }
            coVerify(exactly = 1) { newBook.from(requirement) }
            coVerify(exactly = 1) { event.produce(book) }
            coVerify(exactly = 1) { cache.save(book) }
        }

        scenario("failure uniqueness validation") {
            val error = Error(message = "book ${book.id} already exists", type = Business)

            coEvery {
                uniqueness.validate(requirement)
            } returns error.left()

            createBook.execute(requirement) shouldBeLeft error

            coVerify(exactly = 1) { uniqueness.validate(requirement) }
            coVerify(exactly = 0) { newBook.from(requirement) }
            coVerify(exactly = 0) { event.produce(book) }
            coVerify(exactly = 0) { cache.save(book) }
        }
    }
})
