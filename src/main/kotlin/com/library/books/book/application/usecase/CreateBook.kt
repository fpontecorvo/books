package com.library.books.book.application.usecase

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import com.library.books.book.application.port.inport.CreateBookInPort
import com.library.books.book.application.port.outport.event.BookCreatedEventOutPort
import com.library.books.book.application.service.BookUniquenessValidator
import com.library.books.book.application.service.SaveCachedBookService
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookRequirement
import com.library.books.book.domain.service.BookProvider
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class CreateBook(
    private val uniqueness: BookUniquenessValidator,
    private val provider: BookProvider,
    private val cache: SaveCachedBookService,
    private val event: BookCreatedEventOutPort,
) : CreateBookInPort {
    override suspend fun execute(requirement: BookRequirement): Either<Error, Book> =
        requirement.logs {
            either {
                shouldNotExist()
                    .buildBook()
                    .cache()
                    .produce()
            }
        }

    context(Raise<Error>)
    private suspend fun BookRequirement.shouldNotExist() =
        uniqueness.validate(this)
            .bind()
            .log { info("no existing book matches requirement: {}", it) }

    private suspend fun BookRequirement.buildBook() =
        provider.from(this)
            .log { info("new book build: {}", it.id) }

    context(Raise<Error>)
    private suspend fun Book.produce() =
        event.produce(this)
            .bind()
            .log { info("created book event produced: {}", it.id) }

    context(Raise<Error>)
    private suspend fun Book.cache() =
        cache.save(this)
            .bind()
            .log { info("created book cached: {}", it.id) }

    private suspend fun BookRequirement.logs(action: suspend BookRequirement.() -> Either<Error, Book>) =
        logged(
            description = "create book requirement received",
            leftLog = "error creating book",
            rightLog = "book created",
        ) { action.invoke(this) }

    companion object : CompanionLogger()
}
