package com.library.books.shared.adapter.out.repository.model.mapper

import com.library.books.shared.domain.Page
import com.library.books.shared.domain.Page.Metadata
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.PageImpl as DocumentPage

class FromDocumentPageToPageMapperSpec : StringSpec({

    val mapper = FromDocumentPageToPageMapper()

    "should map DocumentPage to Page with mapped content" {
        val documentPage =
            DocumentPage(
                listOf("item1", "item2"),
            )

        val mapContent: (String) -> Int = { it.length }

        val expectedPage =
            Page(
                content = listOf(5, 5),
                metadata =
                    Metadata(
                        size = 2,
                        totalElements = 2,
                        totalPages = 1,
                        number = 0,
                    ),
            )

        val result = runBlocking { mapper.from(documentPage, mapContent) }

        result shouldBe expectedPage
    }

    "should handle empty DocumentPage correctly" {
        val documentPage = DocumentPage(emptyList<String>())

        val mapContent: (String) -> Int = { it.length }

        val expectedPage =
            Page(
                content = emptyList<String>(),
                metadata =
                    Metadata(
                        size = 0,
                        totalElements = 0,
                        totalPages = 1,
                        number = 0,
                    ),
            )

        val result = runBlocking { mapper.from(documentPage, mapContent) }

        result shouldBe expectedPage
    }
})
