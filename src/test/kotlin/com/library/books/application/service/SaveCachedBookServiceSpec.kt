package com.library.books.application.service

import arrow.core.left
import arrow.core.right
import com.library.books.AUTHOR
import com.library.books.TITLE
import com.library.books.aCreatedBook
import com.library.books.aGenericServerError
import com.library.books.book.application.port.outport.repository.SaveCachedBookOutPort
import com.library.books.book.application.service.SaveCachedBookService
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.service.UniqueBookFilterProvider
import com.library.books.id
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SaveCachedBookServiceSpec : FeatureSpec({

    val uniqueFilter = mockk<UniqueBookFilterProvider>()
    val cache = mockk<SaveCachedBookOutPort>()

    val service =
        SaveCachedBookService(
            uniqueFilter = uniqueFilter,
            cache = cache,
        )

    beforeEach { clearAllMocks() }

    feature("book saved into cache") {

        val book = aCreatedBook()
        val byId = BookFilter(id = id)
        val byUniqueness = BookFilter(title = TITLE, author = AUTHOR)
        val error = aGenericServerError()

        scenario("book save by id and unique criteria") {

            coEvery { uniqueFilter.from(book) } returns byUniqueness
            coEvery { cache.save(book, byId) } returns book.right()
            coEvery { cache.save(book, byUniqueness) } returns book.right()

            service.save(book) shouldBeRight book

            coVerify(exactly = 1) { cache.save(book, byId) }
            coVerify(exactly = 1) { uniqueFilter.from(book) }
            coVerify(exactly = 1) { cache.save(book, byUniqueness) }
        }

        scenario("error saving book by id") {

            coEvery { cache.save(book, byId) } returns error.left()

            service.save(book) shouldBeLeft error

            coVerify(exactly = 1) { cache.save(book, byId) }
            coVerify(exactly = 0) { uniqueFilter.from(any<Book>()) }
            coVerify(exactly = 0) { cache.save(book, byUniqueness) }
        }

        scenario("error saving book by uniqueness") {

            coEvery { uniqueFilter.from(book) } returns byUniqueness
            coEvery { cache.save(book, byId) } returns book.right()
            coEvery { cache.save(book, byUniqueness) } returns error.left()

            service.save(book) shouldBeLeft error

            coVerify(exactly = 1) { cache.save(book, byId) }
            coVerify(exactly = 1) { uniqueFilter.from(book) }
            coVerify(exactly = 1) { cache.save(book, byUniqueness) }
        }
    }
})
