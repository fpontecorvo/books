package com.library.books.book.application.usecase

import arrow.core.left
import arrow.core.right
import com.library.books.aCreatedBook
import com.library.books.book.application.port.outport.repository.SaveBookOutPort
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class SaveBookSpec : FeatureSpec({

    val repository = mockk<SaveBookOutPort>()

    val saveBook = SaveBook(repository)

    beforeEach { clearAllMocks() }

    feature("save book") {
        val book = aCreatedBook()
        scenario("successfully") {
            coEvery { repository.save(book) } returns book.right()
            saveBook.execute(book) shouldBeRight book
            coVerify(exactly = 1) { repository.save(book) }
        }

        scenario("failure") {
            val error = Error(message = "error", type = Business)
            coEvery { repository.save(book) } returns error.left()
            saveBook.execute(book) shouldBeLeft error
            coVerify(exactly = 1) { repository.save(book) }
        }
    }
})
