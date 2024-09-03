package com.library.books.book.application.usecase

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import com.library.books.book.application.port.inport.DeleteBookInPort
import com.library.books.book.application.port.outport.event.BookDeletedEventOutPort
import com.library.books.book.application.service.SaveCachedBookService
import com.library.books.book.domain.Book
import com.library.books.book.domain.service.DeletedBookProvider
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteBook(
    private val deletedBook: DeletedBookProvider,
    private val cache: SaveCachedBookService,
    private val event: BookDeletedEventOutPort,
) : DeleteBookInPort {
    override suspend fun execute(book: Book): Either<Error, Book> =
        book.logs {
            either {
                toDeleted()
                    .produce()
                    .cache()
            }
        }

    private suspend fun Book.toDeleted() =
        deletedBook.from(this)
            .log { info("deleted book build: {}", it.id) }

    context(Raise<Error>)
    private suspend fun Book.produce() =
        event.produce(this)
            .bind()
            .log { info("deleted book event produced: {}", it.id) }

    context(Raise<Error>)
    private suspend fun Book.cache() =
        cache.save(this)
            .bind()
            .log { info("deleted book cached: {}", it.id) }

    private suspend fun Book.logs(action: suspend Book.() -> Either<Error, Book>) =
        logged(
            description = "delete book requirement received",
            leftLog = "error deleting book",
            rightLog = "book deleted",
        ) { action.invoke(this) }

    companion object : CompanionLogger()
}
