package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.aBookFilter
import com.library.books.book.application.port.outport.repository.DeleteCachedBookOutPort
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class DeleteCachedBookSpec : FeatureSpec({

    val cache = mockk<DeleteCachedBookOutPort>()

    val deleteCachedBook = DeleteCachedBook(cache)

    beforeEach { clearAllMocks() }

    feature("delete cache") {
        val filter = aBookFilter()

        scenario("successfully delete") {
            coEvery { cache.delete(filter) } returns Unit.right()

            deleteCachedBook.execute(filter) shouldBeRight Unit

            coVerify(exactly = 1) { cache.delete(filter) }
        }

        scenario("failure delete") {
            val error = Error(message = "error", type = Business)

            coEvery { cache.delete(filter) } returns error.left()

            deleteCachedBook.execute(filter) shouldBeLeft error

            coVerify(exactly = 1) { cache.delete(filter) }
        }
    }
})
