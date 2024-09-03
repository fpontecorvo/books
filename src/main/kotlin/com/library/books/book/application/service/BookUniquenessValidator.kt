package com.library.books.book.application.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.library.books.book.domain.BookFilter
import com.library.books.book.domain.BookRequirement
import com.library.books.book.domain.service.UniqueBookFilterProvider
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import com.library.books.shared.domain.Error.Type.MissingResource
import com.library.books.shared.util.CompanionLogger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class BookUniquenessValidator(
    private val uniqueBookFilterProvider: UniqueBookFilterProvider,
    private val find: FindBookService,
) {
    suspend fun validate(requirement: BookRequirement): Either<Error, BookRequirement> =
        with(requirement) {
            findBy(uniqueness())
                .fold(
                    ifLeft = { if (it.isMissingResource()) right() else it.left() },
                    ifRight = { alreadyExists(it.id).left() },
                ).logEither(
                    { error("error validating book requirement uniqueness: {}", it.message) },
                    { info("book requirement is unique: {}", it) },
                )
        }

    private suspend fun BookRequirement.uniqueness() = uniqueBookFilterProvider.from(this)

    private suspend fun findBy(filter: BookFilter) = find.findBy(filter)

    private fun Error.isMissingResource() = type == MissingResource

    companion object : CompanionLogger() {
        private fun alreadyExists(id: UUID) = Error(message = "book $id already exists", type = Business)
    }
}
