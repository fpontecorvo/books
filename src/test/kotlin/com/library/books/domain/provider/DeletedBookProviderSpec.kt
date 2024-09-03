package com.library.books.domain.provider

import com.library.books.aCreatedBook
import com.library.books.aDeletedBook
import com.library.books.book.domain.service.DeletedBookProvider
import com.library.books.deletedDate
import com.library.books.shared.extensions.AGT_ZONE_ID
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import java.time.OffsetDateTime

class DeletedBookProviderSpec : FeatureSpec({

    val provider = DeletedBookProvider()

    beforeEach { clearAllMocks() }

    feature("provide deleted book from book") {

        scenario("deleted book provided") {

            mockkStatic(OffsetDateTime::class)
            every { OffsetDateTime.now(AGT_ZONE_ID) } returns deletedDate

            provider.from(aCreatedBook()) shouldBe aDeletedBook()
        }
    }
})
