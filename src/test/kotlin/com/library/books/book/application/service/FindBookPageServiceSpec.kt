package com.library.books.book.application.service

import arrow.core.Either
import com.library.books.aCreatedBook
import com.library.books.aDeletedBook
import com.library.books.anUpdatedBook
import com.library.books.book.application.port.outport.repository.FindBookPageOutPort
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

class FindBookPageServiceSpec : StringSpec({

    val repository = mockk<FindBookPageOutPort>()
    val findBookPageService = FindBookPageService(repository)

    "should return a page of books when repository returns successfully" {
        val filter = BookFilter()
        val pageRequirement = PageRequirement(page = 0, size = 10)
        val books = listOf(aCreatedBook(), aDeletedBook(), anUpdatedBook())
        val page = Page(books, Page.Metadata(3, 3, 1, 1))

        coEvery { repository.findBy(filter.excludeDeleted(), pageRequirement) } returns Either.Right(page)

        val result = runBlocking { findBookPageService.findBy(filter, pageRequirement) }

        result shouldBeRight page
    }
})
