package com.library.books.book.application.usecase

import arrow.core.Either
import com.library.books.book.application.port.inport.SaveBookInPort
import com.library.books.book.application.port.outport.repository.SaveBookOutPort
import com.library.books.book.domain.Book
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class SaveBook(
    private val repository: SaveBookOutPort,
) : SaveBookInPort {
    override suspend fun execute(book: Book): Either<Error, Book> =
        book.logs {
            repository.save(this)
        }

    private suspend fun Book.logs(action: suspend Book.() -> Either<Error, Book>) =
        logged(
            description = "save book requirement received",
            leftLog = "error saving book",
            rightLog = "book saved",
        ) { action.invoke(this) }

    companion object : CompanionLogger()
}
