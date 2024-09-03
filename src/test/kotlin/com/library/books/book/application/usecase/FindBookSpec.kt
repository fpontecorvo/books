package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.aCreatedBook
import com.library.books.book.application.service.FindBookService
import com.library.books.book.domain.BookFilter
import com.library.books.id
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class FindBookSpec : FeatureSpec({

    val service = mockk<FindBookService>()

    val findBook = FindBook(service)

    beforeEach { clearAllMocks() }

    feature("find book") {
        val filter = BookFilter(id)
        val book = aCreatedBook()
        scenario("successfully") {
            coEvery { service.findBy(filter) } returns book.right()
            findBook.execute(id) shouldBeRight book
            coVerify(exactly = 1) { service.findBy(filter) }
        }

        scenario("failure") {
            val error = Error(message = "error", type = Business)
            coEvery { service.findBy(filter) } returns error.left()
            findBook.execute(id) shouldBeLeft error
            coVerify(exactly = 1) { service.findBy(filter) }
        }
    }
})
