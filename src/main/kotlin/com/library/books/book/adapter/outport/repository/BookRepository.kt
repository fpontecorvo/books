package com.library.books.book.adapter.outport.repository

import arrow.core.Either
import com.library.books.book.adapter.outport.repository.model.BookQueryFilter
import com.library.books.book.adapter.outport.repository.model.dbError
import com.library.books.book.adapter.outport.repository.model.notFound
import com.library.books.book.adapter.outport.repository.provider.BookQueryProvider
import com.library.books.book.application.port.outport.repository.BookRepositoryOutPort
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.adapter.out.repository.PageableRepository
import com.library.books.shared.adapter.out.repository.model.mapper.FromDocumentPageToPageMapper
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement
import com.library.books.shared.util.CompanionLogger
import com.library.books.shared.util.coCatch
import com.library.books.shared.util.leftIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import java.util.UUID
import org.springframework.data.domain.Page as DocumentPage

@Component
class BookRepository(
    private val coRepository: CoRepository,
    private val mongoTemplate: ReactiveMongoTemplate,
    private val queryProvider: BookQueryProvider,
    private val toPageMapper: FromDocumentPageToPageMapper,
) : BookRepositoryOutPort, PageableRepository<Book>(mongoTemplate) {
    override suspend fun save(book: Book): Either<Error, Book> =
        coCatch(::dbError) {
            coRepository.save(book)
        }
            .logEither(
                { error("error saving book: {}", book.id, it.message) },
                { info("book saved {}", it) },
            )

    override suspend fun findBy(filter: BookFilter): Either<Error, Book> =
        coCatch(::dbError) {
            mongoTemplate.findOne(filter.toQuery(), Book::class.java)
                .awaitSingleOrNull()
        }.leftIfNull(notFound(filter))
            .logEither(
                { error("error searching for book: {}", filter, it.message) },
                { info("book found: {}", it) },
            )

    override suspend fun findBy(
        filter: BookFilter,
        page: PageRequirement,
    ): Either<Error, Page<Book>> =
        findBy(filter.toQuery(), page.toPageable(), Book::class.java)
            .map { it.toPage() }
            .logEither(
                { error("error searching for book page: {}", it.message) },
                { info("book page found: {}", it.metadata) },
            )

    override suspend fun find(): Either<Error, Flow<Book>> =
        coCatch(::dbError) {
            coRepository.findAll()
        }.logEither(
            { error("error books: {}", it.message) },
            { info("books emitted") },
        )

    private suspend fun BookFilter.toQuery() = queryProvider.from(BookQueryFilter.from(this))

    private suspend fun PageRequirement.toPageable() = PageRequest.of(page, size)

    private suspend fun DocumentPage<Book>.toPage() = toPageMapper.from(this) { it }

    companion object : CompanionLogger()
}

interface CoRepository : CoroutineCrudRepository<Book, UUID>
