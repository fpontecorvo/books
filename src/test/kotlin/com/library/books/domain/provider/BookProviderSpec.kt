package com.library.books.domain.provider

import com.library.books.aBookRequirement
import com.library.books.aCreatedBook
import com.library.books.book.domain.service.BookProvider
import com.library.books.createdDate
import com.library.books.id
import com.library.books.shared.extensions.AGT_ZONE_ID
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import java.time.OffsetDateTime
import java.util.UUID

class BookProviderSpec : FeatureSpec({

    val provider = BookProvider()

    beforeEach { clearAllMocks() }

    feature("provide new book from requirement") {

        scenario("new book provided") {

            mockkStatic(OffsetDateTime::class)
            every { OffsetDateTime.now(AGT_ZONE_ID) } returns createdDate
            mockkStatic(UUID::class)
            every { UUID.randomUUID() } returns id

            provider.from(aBookRequirement()) shouldBe aCreatedBook()
        }
    }
})
