package com.library.books.book.application.usecase

import arrow.core.Either
import com.library.books.book.application.port.inport.DeleteAllCachedBookInPort
import com.library.books.book.application.port.outport.repository.DeleteAllCachedBookOutPort
import com.library.books.shared.domain.Error
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component

@Component
class DeleteAllCachedBook(
    private val cache: DeleteAllCachedBookOutPort,
) : DeleteAllCachedBookInPort {
    override suspend fun execute(): Either<Error, Unit> =
        cache.deleteAll()
            .logRight { info("all cached book deleted") }

    companion object : CompanionLogger()
}
