package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.aCreatedBook
import com.library.books.book.application.port.outport.repository.FindBooksOutPort
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class FindAllBooksSpec : FeatureSpec({

    val repository = mockk<FindBooksOutPort>()

    val findBooks = FindAllBooks(repository)

    beforeEach { clearAllMocks() }

    feature("find book") {
        val bookFlow = flowOf(aCreatedBook())

        scenario("successfully") {
            coEvery { repository.find() } returns bookFlow.right()
            findBooks.execute() shouldBeRight bookFlow
            coVerify(exactly = 1) { repository.find() }
        }

        scenario("failure") {
            val error = Error(message = "error", type = Business)
            coEvery { repository.find() } returns error.left()
            findBooks.execute() shouldBeLeft error
            coVerify(exactly = 1) { repository.find() }
        }
    }
})
