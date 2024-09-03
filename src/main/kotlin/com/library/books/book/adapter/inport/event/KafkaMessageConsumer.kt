package com.library.books.book.adapter.inport.event

import arrow.core.Either
import arrow.core.flatMap
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.library.books.shared.domain.EitherErrorException
import com.library.books.shared.domain.Error
import com.library.books.shared.domain.Error.Type.Unhandled
import com.library.books.shared.util.CompanionLogger
import com.library.books.shared.util.catch
import kotlinx.coroutines.CompletableDeferred
import org.springframework.kafka.support.Acknowledgment

abstract class KafkaMessageConsumer(
    val objectMapper: ObjectMapper,
) {
    protected suspend inline fun <reified T : Any> consume(
        payload: String,
        topic: String,
        ack: Acknowledgment,
        deferred: CompletableDeferred<T>,
        action: (T) -> Either<Error, T>,
    ) = log { info("message consumed: Topic: {}, Message: {}", topic, payload) }.let {
        payload.deserialize<T>()
            .flatMap { action(it) }
            .also { ack.acknowledge() }
            .onRight { deferred.complete(it) }
            .onLeft { deferred.completeExceptionally(EitherErrorException(it)) }
    }

    protected inline fun <reified T : Any> String.deserialize(): Either<Error, T> =
        catch(::deserializationError) {
            objectMapper.readValue(this, jacksonTypeRef<T>())
        }

    companion object : CompanionLogger() {
        fun deserializationError(throwable: Throwable) = Error("error deserializing message", type = Unhandled, cause = throwable)
    }
}
