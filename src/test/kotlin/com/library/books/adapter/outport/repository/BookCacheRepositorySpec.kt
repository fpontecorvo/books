package com.library.books.adapter.outport.repository

import com.library.books.AUTHOR
import com.library.books.TITLE
import com.library.books.aCreatedBook
import com.library.books.book.adapter.outport.repository.BookCacheRepository
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.id
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
import io.mockk.mockk
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import reactor.core.publisher.Mono
import java.time.Duration.ofSeconds

class BookCacheRepositorySpec : FeatureSpec({

    val redisTemplate = mockk<ReactiveRedisTemplate<String, Book>>()
    val opsForValue = mockk<ReactiveValueOperations<String, Book>>()
    val ttl = 6000L

    val repository =
        BookCacheRepository(
            redisTemplate = redisTemplate,
            ttl = ttl,
        )

    beforeEach { clearAllMocks() }

    val book = aCreatedBook()
    val keyShapedAuthor = AUTHOR.replace(" ", "_")
    val keyShapedTitle = TITLE.replace(" ", "_")

    val table =
        table(
            headers("filter", "key"),
            row(BookFilter(id = id), "books:$id"),
            row(BookFilter(author = AUTHOR), "books:$keyShapedAuthor"),
            row(BookFilter(title = TITLE), "books:$keyShapedTitle"),
            row(BookFilter(id = id, author = AUTHOR), "books:$id:$keyShapedAuthor"),
            row(BookFilter(id = id, title = TITLE), "books:$id:$keyShapedTitle"),
            row(BookFilter(author = AUTHOR, title = TITLE), "books:$keyShapedAuthor:$keyShapedTitle"),
            row(BookFilter(id = id, author = AUTHOR, title = TITLE), "books:$id:$keyShapedAuthor:$keyShapedTitle"),
        )

    val exception = RuntimeException("an exception")
    val error = Error(message = "error communicating with cache", type = Server, cause = exception)

    feature("save book to cache") {

        scenario("successful save") {
            forAll(table) { filter, key ->
                coEvery { redisTemplate.opsForValue() } returns opsForValue
                coEvery { opsForValue.set(key, book, ofSeconds(ttl)) } returns Mono.just(true)

                repository.save(book, filter) shouldBeRight book

                coVerify(exactly = 1) { redisTemplate.opsForValue() }
                coVerify(exactly = 1) { opsForValue.set(key, book, ofSeconds(ttl)) }
                clearAllMocks()
            }
        }

        scenario("error communicating with redis") {
            forAll(table) { filter, key ->
                coEvery { redisTemplate.opsForValue() } returns opsForValue
                coEvery { opsForValue.set(key, book, ofSeconds(ttl)) } throws exception

                repository.save(book, filter) shouldBeLeft error

                coVerify(exactly = 1) { redisTemplate.opsForValue() }
                coVerify(exactly = 1) { opsForValue.set(key, book, ofSeconds(ttl)) }
                clearAllMocks()
            }
        }
    }

    feature("find book in cache by filter") {

        scenario("found") {
            forAll(table) { filter, key ->
                coEvery { redisTemplate.opsForValue() } returns opsForValue
                coEvery { opsForValue.get(key) } returns Mono.just(book)

                repository.findBy(filter) shouldBeRight book

                coVerify(exactly = 1) { redisTemplate.opsForValue() }
                coVerify(exactly = 1) { opsForValue.get(key) }
                clearAllMocks()
            }
        }

        scenario("error communicating with redis") {
            forAll(table) { filter, key ->
                coEvery { redisTemplate.opsForValue() } returns opsForValue
                coEvery { opsForValue.get(key) } throws exception

                repository.findBy(filter) shouldBeLeft error

                coVerify(exactly = 1) { redisTemplate.opsForValue() }
                coVerify(exactly = 1) { opsForValue.get(key) }
                clearAllMocks()
            }
        }
    }
})
