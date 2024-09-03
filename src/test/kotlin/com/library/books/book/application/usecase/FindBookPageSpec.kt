package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.aCreatedBook
import com.library.books.book.application.service.FindBookPageService
import com.library.books.book.domain.BookFilter
import com.library.books.id
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class FindBookPageSpec : FeatureSpec({

    val service = mockk<FindBookPageService>()

    val findBook = FindBookPage(service)

    beforeEach { clearAllMocks() }

    feature("find book paginated") {
        val filter = BookFilter(id)
        val pageRequirement = PageRequirement(1, 1)
        val book = aCreatedBook()
        val page =
            Page(
                content = listOf(book),
                metadata =
                    Page.Metadata(
                        size = 1,
                        totalElements = 1,
                        totalPages = 1,
                        number = 1,
                    ),
            )
        scenario("successfully") {
            coEvery { service.findBy(filter, pageRequirement) } returns page.right()
            findBook.execute(filter, pageRequirement) shouldBeRight page
            coVerify(exactly = 1) { service.findBy(filter, pageRequirement) }
        }

        scenario("failure") {
            val error = Error(message = "error", type = Business)
            coEvery { service.findBy(filter, pageRequirement) } returns error.left()
            findBook.execute(filter, pageRequirement) shouldBeLeft error
            coVerify(exactly = 1) { service.findBy(filter, pageRequirement) }
        }
    }
})
