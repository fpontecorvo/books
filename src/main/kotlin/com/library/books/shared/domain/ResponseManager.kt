package com.library.books.shared.domain

import arrow.core.Either
import com.library.books.book.adapter.inport.error.ApiErrorResponse
import com.library.books.book.adapter.inport.error.provider.ServerErrorProvider
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.json

suspend fun Either<Error, Any>.handleResponse(
    httpStatus: HttpStatus = OK,
    errorProvider: () -> ServerErrorProvider,
): ServerResponse =
    fold(
        ifLeft = { errorProvider().from(it).response(ApiErrorResponse.from(it)) },
        ifRight = { status(httpStatus).response(it) },
    )

suspend fun ServerResponse.BodyBuilder.response(body: Any) = json().bodyValueAndAwait(body)
