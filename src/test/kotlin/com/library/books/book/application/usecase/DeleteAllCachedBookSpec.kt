package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.book.application.port.outport.repository.DeleteAllCachedBookOutPort
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class DeleteAllCachedBookSpec : FeatureSpec({

    val cache = mockk<DeleteAllCachedBookOutPort>()

    val deleteAllCachedBook = DeleteAllCachedBook(cache)

    beforeEach { clearAllMocks() }

    feature("create book") {

        scenario("successfully creation book") {

            coEvery { cache.deleteAll() } returns Unit.right()

            deleteAllCachedBook.execute() shouldBeRight Unit

            coVerify(exactly = 1) { cache.deleteAll() }
        }

        scenario("failure uniqueness validation") {
            val error = Error(message = "error", type = Business)

            coEvery { cache.deleteAll() } returns error.left()

            deleteAllCachedBook.execute() shouldBeLeft error

            coVerify(exactly = 1) { cache.deleteAll() }
        }
    }
})
