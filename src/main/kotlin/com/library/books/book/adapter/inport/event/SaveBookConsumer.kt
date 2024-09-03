package com.library.books.book.adapter.inport.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.books.book.application.port.inport.SaveBookInPort
import com.library.books.book.domain.Book
import com.library.books.shared.util.CompanionLogger
import com.library.books.shared.util.VT
import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC
import org.springframework.messaging.handler.annotation.Header
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class SaveBookConsumer(
    private val save: SaveBookInPort,
    private val observationRegistry: ObservationRegistry,
    objectMapper: ObjectMapper,
) : KafkaMessageConsumer(objectMapper) {
    @KafkaListener(
        topics = ["\${messages.topic.book.created}", "\${messages.topic.book.deleted}"],
        groupId = "\${messages.group.book.save}",
    )
    @RetryableTopic(
        attempts = "2",
        backoff = Backoff(delay = 1000, multiplier = 2.0),
        retryTopicSuffix = ".persist-retry",
        dltTopicSuffix = ".persist-dlt",
    )
    fun consume(
        message: String,
        @Header(RECEIVED_TOPIC) topic: String,
        ack: Acknowledgment,
    ) = CompletableDeferred<Book>().let {
        CoroutineScope(Dispatchers.VT)
            .launch(observationRegistry.asContextElement()) {
                consume<Book>(message, topic, ack, it) {
                    save.execute(it)
                }
            }

        runBlocking { it.await() }
    }

    @DltHandler
    fun handler(
        message: String,
        @Header(RECEIVED_TOPIC) topic: String,
        ack: Acknowledgment,
    ) {
        log.error("handler a message: {}", message)
        ack.acknowledge()
    }

    companion object : CompanionLogger()
}
