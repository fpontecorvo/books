package com.library.books.book.adapter.inport.error.provider

import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Business
import com.library.books.shared.domain.Error.Type.Input
import com.library.books.shared.domain.Error.Type.MissingResource
import com.library.books.shared.domain.Error.Type.Server
import com.library.books.shared.domain.Error.Type.Unhandled
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.ServerResponse.unprocessableEntity

@Component
class ServerErrorProvider {
    fun from(error: Error): BodyBuilder =
        when (error.type) {
            Business -> unprocessableEntity()
            MissingResource -> status(NOT_FOUND)
            Input -> badRequest()
            Server, Unhandled -> status(INTERNAL_SERVER_ERROR)
        }
}
