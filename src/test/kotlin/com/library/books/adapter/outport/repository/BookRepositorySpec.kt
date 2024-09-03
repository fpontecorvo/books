package com.library.books.adapter.outport.repository

import com.library.books.AUTHOR
import com.library.books.TITLE
import com.library.books.aCreatedBook
import com.library.books.book.adapter.outport.repository.BookRepository
import com.library.books.book.adapter.outport.repository.CoRepository
import com.library.books.book.adapter.outport.repository.model.BookQueryFilter
import com.library.books.book.adapter.outport.repository.provider.BookQueryProvider
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.id
import com.library.books.shared.adapter.out.repository.model.mapper.FromDocumentPageToPageMapper
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Server
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Mono

class BookRepositorySpec : FeatureSpec({

    val coRepository = mockk<CoRepository>()
    val mongoTemplate = mockk<ReactiveMongoTemplate>()
    val queryProvider = mockk<BookQueryProvider>()
    val toPageMapper = mockk<FromDocumentPageToPageMapper>()
    val repository =
        BookRepository(
            coRepository = coRepository,
            mongoTemplate = mongoTemplate,
            queryProvider = queryProvider,
            toPageMapper = toPageMapper,
        )

    beforeEach { clearAllMocks() }

    val book = aCreatedBook()
    val exception = RuntimeException("an exception")
    val error = Error(message = "error communicating with db", type = Server, cause = exception)

    feature("save book") {

        scenario("successful save") {
            coEvery { coRepository.save(book) } returns book

            repository.save(book) shouldBeRight book

            coVerify(exactly = 1) { coRepository.save(book) }
        }

        scenario("error communicating with mongo") {
            coEvery { coRepository.save(book) } throws exception

            repository.save(book) shouldBeLeft error

            coVerify(exactly = 1) { coRepository.save(book) }
        }
    }

    feature("find book by filter") {

        scenario("found") {
            forAll(scenarios) { filter, queryFilter, query ->
                coEvery { queryProvider.from(queryFilter) } returns query
                every { mongoTemplate.findOne(query, Book::class.java) } returns Mono.just(book)

                repository.findBy(filter) shouldBeRight book

                coVerify(exactly = 1) { queryProvider.from(queryFilter) }
                verify(exactly = 1) { mongoTemplate.findOne(query, Book::class.java) }
            }
        }

        scenario("error communicating with mongo") {
            forAll(scenarios) { filter, queryFilter, query ->
                coEvery { queryProvider.from(queryFilter) } returns query
                every { mongoTemplate.findOne(query, Book::class.java) } throws exception

                repository.findBy(filter) shouldBeLeft error

                coVerify(exactly = 1) { queryProvider.from(queryFilter) }
                verify(exactly = 1) { mongoTemplate.findOne(query, Book::class.java) }
            }
        }
    }
})

private val whereId = where("_id").`is`(id)
private val whereAuthor = where("author").`is`(AUTHOR)
private val whereTitle = where("title").`is`(TITLE)
private val scenarios =
    table(
        headers = headers("filter", "queryFilter", "query"),
        row(
            BookFilter(id = id),
            BookQueryFilter(id = id),
            Query().addCriteria(whereId),
        ),
        row(
            BookFilter(author = AUTHOR),
            BookQueryFilter(author = AUTHOR),
            Query().addCriteria(whereAuthor),
        ),
        row(
            BookFilter(title = TITLE),
            BookQueryFilter(title = TITLE),
            Query().addCriteria(whereTitle),
        ),
        row(
            BookFilter(id = id, author = AUTHOR),
            BookQueryFilter(id = id, author = AUTHOR),
            Query().addCriteria(whereId).addCriteria(whereAuthor),
        ),
        row(
            BookFilter(id = id, title = TITLE),
            BookQueryFilter(id = id, title = TITLE),
            Query().addCriteria(whereId).addCriteria(whereTitle),
        ),
        row(
            BookFilter(author = AUTHOR, title = TITLE),
            BookQueryFilter(author = AUTHOR, title = TITLE),
            Query().addCriteria(whereAuthor).addCriteria(whereTitle),
        ),
        row(
            BookFilter(id = id, author = AUTHOR, title = TITLE),
            BookQueryFilter(id = id, author = AUTHOR, title = TITLE),
            Query().addCriteria(whereId).addCriteria(whereAuthor).addCriteria(whereTitle),
        ),
    )
