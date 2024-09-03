package com.library.books.book.adapter.outport.event

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.library.books.book.domain.Book
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Unhandled
import com.library.books.shared.util.CompanionLogger
import com.library.books.shared.util.coCatch
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class Producer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    suspend fun produce(
        book: Book,
        topic: String,
    ): Either<Error, Book> =
        coCatch(Companion::error) {
            book.send(topic)
        }.logRight { info("{} event produced for: {}", topic, book.id) }

    private fun Book.send(topic: String) =
        kafkaTemplate.send(
            // topic =
            topic,
            // key =
            id.toString(),
            // data =
            asMessage(),
        ).let { this }

    private fun Book.asMessage() = objectMapper.writeValueAsString(this)

    companion object : CompanionLogger() {
        const val TRACE = "trace"

        private fun error(throwable: Throwable) = Error("error communicating with kafka", type = Unhandled, cause = throwable)
    }
}
