package com.library.books.domain.provider

import com.library.books.AUTHOR
import com.library.books.TITLE
import com.library.books.aBookRequirement
import com.library.books.aCreatedBook
import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.service.UniqueBookFilterProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class UniqueBookFilterProviderSpec : FeatureSpec({

    val provider = UniqueBookFilterProvider()

    feature("provide unique book criteria filter") {

        scenario("from book") {
            provider.from(aCreatedBook()) shouldBe BookFilter(title = TITLE, author = AUTHOR)
        }

        scenario("from book requirement") {
            provider.from(aBookRequirement()) shouldBe BookFilter(title = TITLE, author = AUTHOR)
        }
    }
})
