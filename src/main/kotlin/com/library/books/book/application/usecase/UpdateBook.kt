package com.library.books.book.application.usecase

import arrow.core.Either
import arrow.core.right
import com.library.books.book.application.port.inport.UpdateBookInPort
import com.library.books.book.application.port.outport.event.BookCreatedEventOutPort
import com.library.books.book.application.service.SaveCachedBookService
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookRequirement
import com.library.books.book.domain.service.UpdatedBookProvider
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class UpdateBook(
    private val updatedBook: UpdatedBookProvider,
    private val event: BookCreatedEventOutPort,
    private val cache: SaveCachedBookService,
) : UpdateBookInPort {
    override suspend fun execute(
        requirement: BookRequirement,
        book: Book,
    ): Either<Error, Book> =
        requirement.log {
            book.shouldUpdateWith(requirement) { book ->
                book.updatedWith(requirement)
                    .produce()
                    .onRight { it.cache() }
            }
        }

    private suspend fun Book.updatedWith(requirement: BookRequirement) = updatedBook.from(requirement, this)

    private suspend fun Book.shouldUpdateWith(
        requirement: BookRequirement,
        doUpdate: suspend (Book) -> Either<Error, Book>,
    ) = if (BookRequirement.from(this) == requirement) {
        right()
    } else {
        doUpdate(this)
    }

    private suspend fun Book.produce() = event.produce(this)

    private suspend fun Book.cache() = cache.save(this)

    private suspend fun BookRequirement.log(action: suspend BookRequirement.() -> Either<Error, Book>) =
        logged(
            description = "update book requirement received",
            leftLog = "error updating book",
            rightLog = "book updated",
        ) { action.invoke(this) }

    companion object : CompanionLogger()
}
