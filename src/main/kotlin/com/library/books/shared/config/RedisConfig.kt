package com.library.books.shared.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,
    @Value("\${spring.data.redis.port}")
    private val port: Int,
) {
    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisTemplate<String, *> =
        LettuceConnectionFactory(host, port).let {
            it.afterPropertiesSet()
            ReactiveRedisTemplate(it, redisSerializationContext<Any>())
        }

    private inline fun <reified T> redisSerializationContext(): RedisSerializationContext<String, T> =
        StringRedisSerializer().let { keySerializer ->
            Jackson2JsonRedisSerializer(objectMapper(), T::class.java).let { valueSerializer ->
                RedisSerializationContext.newSerializationContext<String, T>()
                    .key(keySerializer)
                    .hashKey(keySerializer)
                    .value(valueSerializer)
                    .hashValue(valueSerializer)
                    .build()
            }
        }

    companion object {
        private fun objectMapper() =
            ObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build(),
                    ObjectMapper.DefaultTyping.EVERYTHING,
                    JsonTypeInfo.As.PROPERTY,
                )
    }
}
