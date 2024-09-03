package com.library.books.application.service

import arrow.core.left
import arrow.core.right
import com.library.books.aCreatedBook
import com.library.books.aDeletedBook
import com.library.books.aGenericServerError
import com.library.books.book.application.port.outport.repository.FindBookByFilterOutPort
import com.library.books.book.application.port.outport.repository.FindCachedBookByFilterOutPort
import com.library.books.book.application.service.FindBookService
import com.library.books.book.domain.BookFilter
import com.library.books.id
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.MissingResource
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class FindBookByIdServiceSpec : FeatureSpec({

    val cache = mockk<FindCachedBookByFilterOutPort>()
    val repository = mockk<FindBookByFilterOutPort>()

    val service =
        FindBookService(
            cache = cache,
            repository = repository,
        )

    beforeEach { clearAllMocks() }

    feature("find book by filter") {

        val filter = BookFilter(id = id)
        val created = aCreatedBook()
        val deleted = aDeletedBook()
        val notFound = Error(message = "book not found: $filter", type = MissingResource)

        scenario("found in cache") {
            coEvery { cache.findBy(filter) } returns created.right()

            service.findBy(filter) shouldBeRight created

            coVerify(exactly = 1) { cache.findBy(filter) }
            coVerify(exactly = 0) { repository.findBy(any()) }
        }

        scenario("error searching cache, found in repository") {
            val error = aGenericServerError()

            coEvery { cache.findBy(filter) } returns error.left()
            coEvery { repository.findBy(filter) } returns created.right()

            service.findBy(filter) shouldBeRight created

            coVerify(exactly = 1) { cache.findBy(filter) }
            coVerify(exactly = 1) { repository.findBy(filter) }
        }

        scenario("error searching cache and repository") {
            val error = aGenericServerError()

            coEvery { cache.findBy(filter) } returns error.left()
            coEvery { repository.findBy(filter) } returns error.left()

            service.findBy(filter) shouldBeLeft error

            coVerify(exactly = 1) { cache.findBy(filter) }
            coVerify(exactly = 1) { repository.findBy(filter) }
        }

        scenario("found deleted in cache") {
            coEvery { cache.findBy(filter) } returns deleted.right()

            service.findBy(filter) shouldBeLeft notFound

            coVerify(exactly = 1) { cache.findBy(filter) }
            coVerify(exactly = 0) { repository.findBy(filter) }
        }

        scenario("error searching cache, found deleted in repository") {
            val error = aGenericServerError()

            coEvery { cache.findBy(filter) } returns error.left()
            coEvery { repository.findBy(filter) } returns deleted.right()

            service.findBy(filter) shouldBeLeft notFound

            coVerify(exactly = 1) { cache.findBy(filter) }
            coVerify(exactly = 1) { repository.findBy(filter) }
        }
    }
})
