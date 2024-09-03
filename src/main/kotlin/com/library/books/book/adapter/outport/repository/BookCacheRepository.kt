package com.library.books.book.adapter.outport.repository

import arrow.core.Either
import com.library.books.book.adapter.outport.repository.model.CachedBookFilter.Companion.from
import com.library.books.book.adapter.outport.repository.model.cacheError
import com.library.books.book.adapter.outport.repository.model.notFound
import com.library.books.book.application.port.outport.repository.BookCacheRepositoryOutPort
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import com.library.books.shared.util.coCatch
import com.library.books.shared.util.leftIfNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration.ofSeconds

@Component
class BookCacheRepository(
    @Qualifier("reactiveRedisConnectionFactory")
    private val redisTemplate: ReactiveRedisTemplate<String, Book>,
    @Value("\${book.config.cache.ttl}")
    private val ttl: Long,
) : BookCacheRepositoryOutPort {
    override suspend fun save(
        book: Book,
        identifier: BookFilter,
    ): Either<Error, Book> =
        with(book) {
            coCatch(::cacheError) {
                redisTemplate.opsForValue().set(
                    // key =
                    identifier.key(),
                    // value =
                    book,
                    // timeout =
                    ofSeconds(ttl),
                ).awaitSingleOrNull()
            }.map { this }
        }.logEither(
            { error("error saving cached book: {}", it.message) },
            { info("cached book saved: {}", it) },
        )

    override suspend fun findBy(identifier: BookFilter): Either<Error, Book> =
        identifier.key().let { key ->
            coCatch(::cacheError) {
                redisTemplate
                    .opsForValue()
                    .get(key)
                    .awaitSingleOrNull()
            }.leftIfNull(notFound(key))
        }.logEither(
            { error("error searching cached book: {}", it.message) },
            { info("cached book found: {}", it) },
        )

    override suspend fun delete(identifier: BookFilter): Either<Error, Unit> =
        coCatch(::cacheError) {
            Unit.also {
                redisTemplate
                    .delete(identifier.key())
                    .awaitSingle()
            }
        }
            .logEither(
                { error("error deleting cached book: {}", it.message) },
                { info("cached book deleted") },
            )

    override suspend fun deleteAll(): Either<Error, Unit> =
        coCatch(::cacheError) {
            redisTemplate.keys(BookFilter().key()).asFlow()
                .onEach { redisTemplate.delete(it).awaitSingle() }
                .collect()
        }.logEither(
            { error("error deleting all cached books: {}", it.message) },
            { info("all cached books deleted") },
        )

    private fun BookFilter.key() = from(this).key()

    companion object : CompanionLogger()
}
