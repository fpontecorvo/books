package com.library.books.domain.provider

import com.library.books.aCreatedBook
import com.library.books.anUpdateBookRequirement
import com.library.books.anUpdatedBook
import com.library.books.book.domain.service.UpdatedBookProvider
import com.library.books.shared.extensions.AGT_ZONE_ID
import com.library.books.updatedDate
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import java.time.OffsetDateTime

class UpdatedBookProviderSpec : FeatureSpec({

    val provider = UpdatedBookProvider()

    beforeEach { clearAllMocks() }

    feature("update book") {

        scenario("successful update") {

            mockkStatic(OffsetDateTime::class)
            every { OffsetDateTime.now(AGT_ZONE_ID) } returns updatedDate

            provider.from(
                anUpdateBookRequirement(),
                aCreatedBook(),
            ) shouldBe anUpdatedBook()
        }
    }
})
