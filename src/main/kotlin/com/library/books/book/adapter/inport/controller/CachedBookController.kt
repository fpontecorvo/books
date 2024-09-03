package com.library.books.book.adapter.inport.controller

import com.library.books.book.application.port.inport.DeleteAllCachedBookInPort
import com.library.books.book.application.port.inport.DeleteCachedBookInPort
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/books/cached")
class CachedBookController(
    private val delete: DeleteCachedBookInPort,
    private val deleteAll: DeleteAllCachedBookInPort,
) {
    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteAll(): Unit? = deleteAll.execute().getOrNull()
}
