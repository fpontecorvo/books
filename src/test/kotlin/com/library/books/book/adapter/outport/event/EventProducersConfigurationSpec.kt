package com.library.books.book.adapter.outport.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.books.aCreatedBook
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.kafka.core.KafkaTemplate

class EventProducersConfigurationSpec : StringSpec({

    val kafkaTemplate = mockk<KafkaTemplate<String, String>>(relaxed = true)
    val objectMapper = mockk<ObjectMapper>(relaxed = true)
    val deleteTopic = "delete-topic"
    val createdTopic = "created-topic"

    val eventProducersConfiguration =
        EventProducersConfiguration(
            kafkaTemplate = kafkaTemplate,
            objectMapper = objectMapper,
            deleteTopic = deleteTopic,
            createdTopic = createdTopic,
        )

    "should create a BookCreatedEventOutPort bean and send the message to the correct topic" {
        val bookCreatedEventOutPort = eventProducersConfiguration.bookCreatedEventProducer()

        // Create a mock event
        val event = aCreatedBook()
        every { objectMapper.writeValueAsString(event) } returns event.toString()

        // Produce the event
        bookCreatedEventOutPort.produce(event)

        // Verify that the event was sent to the correct topic
        verify { kafkaTemplate.send(createdTopic, event.id.toString(), event.toString()) }
    }

    "should create a BookDeletedEventOutPort bean and send the message to the correct topic" {
        val bookDeletedEventOutPort = eventProducersConfiguration.bookDeletedEventProducer()

        // Create a mock event
        val event = aCreatedBook()
        every { objectMapper.writeValueAsString(event) } returns event.toString()

        // Produce the event
        bookDeletedEventOutPort.produce(event)

        // Verify that the event was sent to the correct topic
        verify { kafkaTemplate.send(createdTopic, event.id.toString(), event.toString()) }
    }
})
