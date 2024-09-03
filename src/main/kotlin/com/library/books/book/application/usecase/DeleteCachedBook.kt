package com.library.books.book.application.usecase

import arrow.core.Either
import com.library.books.book.application.port.inport.DeleteCachedBookInPort
import com.library.books.book.application.port.outport.repository.DeleteCachedBookOutPort
import com.library.books.book.domain.BookFilter
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteCachedBook(
    private val cache: DeleteCachedBookOutPort,
) : DeleteCachedBookInPort {
    override suspend fun execute(identifier: BookFilter): Either<Error, Unit> =
        cache.delete(identifier)
            .logRight { info("cached book deleted") }

    companion object : CompanionLogger()
}
