package com.library.books.application.service

import arrow.core.left
import arrow.core.right
import com.library.books.AUTHOR
import com.library.books.TITLE
import com.library.books.aBookRequirement
import com.library.books.aCreatedBook
import com.library.books.aGenericMissingResource
import com.library.books.aGenericServerError
import com.library.books.book.application.service.BookUniquenessValidator
import com.library.books.book.application.service.FindBookService
import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.service.UniqueBookFilterProvider
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

class BookUniquenessValidatorSpec : FeatureSpec({

    val uniqueBookFilterProvider = mockk<UniqueBookFilterProvider>()
    val find = mockk<FindBookService>()

    val validator =
        BookUniquenessValidator(
            uniqueBookFilterProvider = uniqueBookFilterProvider,
            find = find,
        )

    beforeEach { clearAllMocks() }

    feature("validate no existing book matches requirement") {

        val requirement = aBookRequirement()
        val filter = BookFilter(title = TITLE, author = AUTHOR)
        val notFound = aGenericMissingResource()

        scenario("no book matches requirement") {

            coEvery { uniqueBookFilterProvider.from(requirement) } returns filter
            coEvery { find.findBy(filter) } returns notFound.left()

            validator.validate(requirement) shouldBeRight requirement

            coVerify(exactly = 1) { uniqueBookFilterProvider.from(requirement) }
            coVerify(exactly = 1) { find.findBy(filter) }
        }

        scenario("existing book matches requirement") {

            coEvery { uniqueBookFilterProvider.from(requirement) } returns filter
            coEvery { find.findBy(filter) } returns aCreatedBook().right()

            validator.validate(requirement) shouldBeLeft Error(message = "book $id already exists", type = Business)

            coVerify(exactly = 1) { uniqueBookFilterProvider.from(requirement) }
            coVerify(exactly = 1) { find.findBy(filter) }
        }

        scenario("error searching book") {
            val error = aGenericServerError()

            coEvery { uniqueBookFilterProvider.from(requirement) } returns filter
            coEvery { find.findBy(filter) } returns error.left()

            validator.validate(requirement) shouldBeLeft error

            coVerify(exactly = 1) { uniqueBookFilterProvider.from(requirement) }
            coVerify(exactly = 1) { find.findBy(filter) }
        }
    }
})
