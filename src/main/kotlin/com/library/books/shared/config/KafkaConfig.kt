package com.library.books.shared.config

import io.micrometer.common.KeyValues
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.micrometer.KafkaRecordSenderContext
import org.springframework.kafka.support.micrometer.KafkaTemplateObservationConvention

@EnableKafka
@Configuration
@ConfigurationPropertiesScan
class KafkaConfig {
    @Primary
    @Bean
    fun observationContainerFactory(kafkaProperties: KafkaProperties): ConcurrentKafkaListenerContainerFactory<String, String> =
        ConcurrentKafkaListenerContainerFactory<String, String>()
            .apply {
                consumerFactory =
                    DefaultKafkaConsumerFactory(
                        kafkaProperties.properties as Map<String, Any>,
                    )
                containerProperties.isObservationEnabled = true
            }

    @Primary
    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> =
        KafkaTemplate(producerFactory).apply {
            setObservationEnabled(true)
            setObservationConvention(
                object : KafkaTemplateObservationConvention {
                    override fun getLowCardinalityKeyValues(context: KafkaRecordSenderContext): KeyValues {
                        return KeyValues.of(
                            "topic",
                            context.destination,
                            "id",
                            context.record.key().toString(),
                        )
                    }
                },
            )
        }
}
