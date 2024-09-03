package com.library.books.book.application.usecase

import arrow.core.Either
import com.library.books.book.application.port.inport.FindBooksInPort
import com.library.books.book.application.port.outport.repository.FindBooksOutPort
import com.library.books.book.domain.Book
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Component

@Component
class FindAllBooks(
    private val repository: FindBooksOutPort,
) : FindBooksInPort {
    override suspend fun execute(): Either<Error, Flow<Book>> = log { repository.find() }

    private suspend fun log(action: suspend () -> Either<Error, Flow<Book>>) =
        logged(
            description = "find all books",
            leftLog = "error finding books",
            rightLog = "books found",
        ) { action.invoke() }

    companion object : CompanionLogger()
}
