package com.library.books.adapter.outport.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.books.aCreatedBook
import com.library.books.book.adapter.outport.event.Producer
import com.library.books.id
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Unhandled
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate

class BookEventProducerSpec : FeatureSpec({

    val kafkaTemplate = mockk<KafkaTemplate<String, String>>()
    val objectMapper = mockk<ObjectMapper>()
    val topic = "a.book.topic"

    val producer =
        Producer(
            kafkaTemplate = kafkaTemplate,
            objectMapper = objectMapper,
        )

    beforeEach { clearAllMocks() }

    feature("produce book event") {

        val book = aCreatedBook()
        val serialized = "a serialized book"

        scenario("successful produce") {
            every { objectMapper.writeValueAsString(book) } returns serialized
            every { kafkaTemplate.send(topic, id.toString(), serialized) } returns mockk()

            producer.produce(book, topic) shouldBeRight book

            verify(exactly = 1) { objectMapper.writeValueAsString(book) }
            verify(exactly = 1) { kafkaTemplate.send(topic, id.toString(), serialized) }
        }

        scenario("error communicating with kafka") {
            val exception = RuntimeException("an exception")
            val error = Error(message = "error communicating with kafka", type = Unhandled, cause = exception)

            every { objectMapper.writeValueAsString(book) } returns serialized
            every { kafkaTemplate.send(topic, id.toString(), serialized) } throws exception

            producer.produce(book, topic) shouldBeLeft error

            verify(exactly = 1) { objectMapper.writeValueAsString(book) }
            verify(exactly = 1) { kafkaTemplate.send(topic, id.toString(), serialized) }
        }
    }
})
