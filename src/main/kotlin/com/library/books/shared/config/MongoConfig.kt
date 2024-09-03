package com.library.books.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.Date

@Configuration
class MongoConfig {
    @Bean
    fun mongoCustomConversions(): MongoCustomConversions =
        MongoCustomConversions(
            listOf(
                OffsetDateTimeWriteConverter(),
                OffsetDateTimeReadConverter(),
            ),
        )
}

internal class OffsetDateTimeWriteConverter : Converter<OffsetDateTime, Date> {
    override fun convert(source: OffsetDateTime): Date = Date.from(source.toInstant())
}

internal class OffsetDateTimeReadConverter : Converter<Date, OffsetDateTime> {
    override fun convert(source: Date): OffsetDateTime = source.toInstant().atOffset(UTC)
}
