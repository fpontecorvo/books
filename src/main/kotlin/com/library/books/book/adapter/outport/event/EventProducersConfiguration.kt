package com.library.books.book.adapter.outport.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.books.book.application.port.outport.event.BookCreatedEventOutPort
import com.library.books.book.application.port.outport.event.BookDeletedEventOutPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate

@Configuration
class EventProducersConfiguration(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    @Value("\${messages.topic.book.deleted}")
    private val deleteTopic: String,
    @Value("\${messages.topic.book.created}")
    private val createdTopic: String,
) {
    @Bean
    fun bookCreatedEventProducer(): BookCreatedEventOutPort =
        Producer(
            kafkaTemplate = kafkaTemplate,
            objectMapper = objectMapper,
        ).run {
            BookCreatedEventOutPort { produce(it, createdTopic) }
        }

    @Bean
    fun bookDeletedEventProducer(): BookDeletedEventOutPort =
        Producer(
            kafkaTemplate = kafkaTemplate,
            objectMapper = objectMapper,
        ).run {
            BookDeletedEventOutPort { produce(it, deleteTopic) }
        }
}
