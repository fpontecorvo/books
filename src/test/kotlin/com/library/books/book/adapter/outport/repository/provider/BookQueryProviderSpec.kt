package com.library.books.book.adapter.outport.repository.provider

import com.library.books.book.adapter.outport.repository.model.BookQueryFilter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.springframework.data.mongodb.core.query.Query

class BookQueryProviderSpec : StringSpec({

    val bookQueryProvider = BookQueryProvider()

    "should create query if filter has criteria" {
        val filter = mockk<BookQueryFilter>()

        every { filter.fieldByCriteria() } returns mapOf()

        val result = runBlocking { bookQueryProvider.from(filter) }

        result shouldBe Query()
    }

    "should create an empty query if filter has no criteria" {
        val filter = mockk<BookQueryFilter>()

        every { filter.fieldByCriteria() } returns emptyMap()

        val result = runBlocking { bookQueryProvider.from(filter) }

        result shouldBe Query()
    }
})
