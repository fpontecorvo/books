package com.library.books.shared.adapter.out.repository

import arrow.core.Either
import arrow.core.flatMap
import com.library.books.book.adapter.outport.repository.model.dbError
import com.library.books.shared.domain.Error
import com.library.books.shared.util.catch
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query

abstract class PageableRepository<T : Any>(
    private val mongoTemplate: ReactiveMongoTemplate,
) {
    protected suspend fun findBy(
        query: Query,
        pageable: Pageable,
        type: Class<T>,
    ): Either<Error, Page<T>> =
        with(query) {
            count(type).flatMap { total ->
                with(pageable).find(type).map { content ->
                    PageImpl(content.asFlow().toList(), pageable, total.awaitFirstOrDefault(0))
                }
            }
        }

    private fun Query.count(type: Class<T>) =
        catch(::dbError) {
            mongoTemplate.count(this, type)
        }

    private fun Query.find(type: Class<T>) =
        catch(::dbError) {
            mongoTemplate.find(this, type)
        }
}
