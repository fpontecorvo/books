package com.library.books.shared.domain

data class Error(
    val message: String,
    val type: Type,
    val cause: Throwable? = null,
) {
    enum class Type {
        Business,
        MissingResource,
        Input,
        Unhandled,
        Server,
    }
}

fun genericServerError() = Error("server error", Error.Type.Server)

class EitherErrorException(val error: Error) : Throwable()
