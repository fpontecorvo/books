package com.library.books.book.adapter.inport.controller

import arrow.core.Either
import arrow.core.flatMap
import com.library.books.book.adapter.inport.controller.model.BookFilterParams
import com.library.books.book.adapter.inport.controller.model.BookRequest
import com.library.books.book.adapter.inport.controller.model.BookResponse
import com.library.books.book.adapter.inport.error.provider.ServerErrorProvider
import com.library.books.book.application.port.inport.CreateBookInPort
import com.library.books.book.application.port.inport.DeleteBookInPort
import com.library.books.book.application.port.inport.FindBookInPort
import com.library.books.book.application.port.inport.FindBookPageInPort
import com.library.books.book.application.port.inport.FindBooksInPort
import com.library.books.book.domain.Book
import com.library.books.book.domain.BookFilter
import com.library.books.shared.adapter.inport.controller.model.PageResponse
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Page
import com.library.books.shared.domain.PageRequirement
import com.library.books.shared.domain.handleResponse
import com.library.books.shared.extensions.suspendFlow
import com.library.books.shared.util.CompanionLogger
import com.library.books.shared.util.VT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.json
import java.util.UUID.fromString
import kotlin.jvm.optionals.getOrDefault
import com.library.books.book.adapter.inport.controller.model.BookResponse.Companion.from as toResponse

@Component
class BooksHandler(
    private val create: CreateBookInPort,
    private val delete: DeleteBookInPort,
    private val findPage: FindBookPageInPort,
    private val find: FindBookInPort,
    private val findAll: FindBooksInPort,
    private val errorProvider: ServerErrorProvider,
) {
    suspend fun create(request: ServerRequest): ServerResponse =
        withContext(Dispatchers.VT) {
            with(request.awaitBody<BookRequest>()) {
                create.execute(toRequirement())
                    .map(::toResponse)
                    .handleResponse(CREATED) { errorProvider }
            }
        }

    suspend fun delete(request: ServerRequest): ServerResponse =
        withContext(Dispatchers.VT) {
            with(fromString(request.pathVariable("id"))) {
                find.execute(this@with)
                    .flatMap { delete.execute(it) }
                    .map(::toResponse)
                    .handleResponse(ACCEPTED) { errorProvider }
            }
        }

    suspend fun findBy(request: ServerRequest): ServerResponse =
        withContext(Dispatchers.VT) {
            with(fromString(request.pathVariable("id"))) {
                find.execute(this@with).map(::toResponse)
                    .handleResponse { errorProvider }
            }
        }

    suspend fun findPage(request: ServerRequest): ServerResponse =
        withContext(Dispatchers.VT) {
            with(request.awaitBodyOrNull<BookFilterParams>()) {
                findPage.execute(
                    filter = this?.toFilter() ?: BookFilter(),
                    page =
                        PageRequirement(
                            page = request.queryParam("page").map { it.toInt() }.getOrDefault(0),
                            size = request.queryParam("size").map { it.toInt() }.getOrDefault(10),
                        ),
                ).handlePageResponse()
            }
        }

    suspend fun find(request: ServerRequest): ServerResponse =
        withContext(Dispatchers.VT) {
            findAll.execute()
                .getOrNull()
                .handleResponse()
        }

    private suspend fun Flow<Book>?.handleResponse() =
        suspendFlow { this?.map(::toResponse) }
            .let { ok().json().bodyAndAwait<BookResponse>(it) }

    private suspend fun Either<Error, Page<Book>>.handlePageResponse() =
        map { PageResponse.from(it, ::toResponse) }
            .handleResponse { errorProvider }

    companion object : CompanionLogger()
}
