package com.library.books.book.adapter.inport.controller

import com.library.books.book.adapter.inport.controller.model.BookRequest
import com.library.books.book.adapter.inport.controller.model.BookResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.FlowPreview
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_NDJSON
import org.springframework.web.bind.annotation.RequestMethod.DELETE
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import java.util.UUID

@Configuration
class RouterConfig(
    private val booksHandler: BooksHandler,
) {
    @FlowPreview
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/v2/books",
            method = arrayOf(POST),
            beanClass = BooksHandler::class,
            beanMethod = "create",
            operation =
                Operation(
                    operationId = "create",
                    responses = [
                        ApiResponse(
                            responseCode = "200",
                            description = "successful operation",
                            content = [Content(schema = Schema(implementation = BookResponse::class))],
                        ),
                        ApiResponse(responseCode = "400", description = "Invalid Book request"),
                        ApiResponse(responseCode = "422", description = "Something was wrong"),
                    ],
                    requestBody = RequestBody(content = [Content(schema = Schema(implementation = BookRequest::class))]),
                ),
        ),
        RouterOperation(
            path = "/v2/books/{id}",
            method = arrayOf(DELETE),
            beanClass = BooksHandler::class,
            beanMethod = "delete",
            operation =
                Operation(
                    operationId = "delete",
                    responses = [
                        ApiResponse(
                            responseCode = "200",
                            description = "successful operation",
                            content = [Content(schema = Schema(implementation = BookResponse::class))],
                        ),
                        ApiResponse(responseCode = "400", description = "Invalid Book request"),
                        ApiResponse(responseCode = "422", description = "Something was wrong"),
                    ],
                    parameters = [
                        Parameter(
                            name = "id",
                            `in` = PATH,
                            schema = Schema(implementation = UUID::class),
                            required = true,
                        ),
                    ],
                ),
        ),
        RouterOperation(
            path = "/v2/books/paged",
            method = arrayOf(GET),
            beanClass = BooksHandler::class,
            beanMethod = "findPage",
        ),
        RouterOperation(
            path = "/v2/books/{id}",
            method = arrayOf(GET),
            beanClass = BooksHandler::class,
            beanMethod = "findBy",
            operation =
                Operation(
                    operationId = "delete",
                    responses = [
                        ApiResponse(
                            responseCode = "200",
                            description = "successful operation",
                            content = [Content(schema = Schema(implementation = BookResponse::class))],
                        ),
                        ApiResponse(responseCode = "400", description = "Invalid Book request"),
                        ApiResponse(responseCode = "422", description = "Something was wrong"),
                    ],
                    parameters = [
                        Parameter(
                            name = "id",
                            `in` = PATH,
                            schema = Schema(implementation = UUID::class),
                            required = true,
                        ),
                    ],
                ),
        ),
        RouterOperation(
            path = "/v2/books",
            method = arrayOf(GET),
            beanClass = BooksHandler::class,
            beanMethod = "find",
        ),
    )
    fun routerFunction(): RouterFunction<ServerResponse> =
        coRouter {
            "/v2/books".nest {
                with(booksHandler) {
                    POST("", ::create)
                    DELETE("/{id}", ::delete)
                    GET("/paged", ::findPage)
                    GET("/{id}", ::findBy)
                    accept(APPLICATION_NDJSON).nest {
                        GET("", ::find)
                    }
                }
            }
        }
}
