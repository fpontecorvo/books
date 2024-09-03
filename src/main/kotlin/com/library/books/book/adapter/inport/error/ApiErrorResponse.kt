package com.library.books.book.adapter.inport.error

import com.library.books.shared.domain.Error
import com.library.books.shared.extensions.AGT_ZONE_ID
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now

data class ApiErrorResponse(
    val datetime: OffsetDateTime,
    val errors: List<ApiError>,
) {
    data class ApiError(val message: String)

    companion object {
        fun from(error: Error) =
            ApiErrorResponse(
                datetime = now(AGT_ZONE_ID),
                errors = listOf(ApiError(message = error.message)),
            )
    }
}
