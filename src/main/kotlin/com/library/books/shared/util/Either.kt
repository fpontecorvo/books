package com.library.books.shared.util

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.library.books.shared.domain.EitherErrorException
import com.library.books.shared.domain.Error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KFunction1

fun <L, R> Either<L, R>.leftIf(
    left: L,
    validation: R.() -> Boolean,
): Either<L, R> =
    flatMap {
        if (validation(it)) {
            left.left()
        } else {
            it.right()
        }
    }

fun <L, R> Either<L, R?>.leftIfNull(left: L): Either<L, R> = flatMap { it?.right() ?: left.left() }

suspend fun <L, R> Either<L, R>.flatMapLeft(f: suspend (left: L) -> Either<L, R>): Either<L, R> =
    when (this) {
        is Right -> this
        is Left -> f(this.value)
    }

suspend inline fun <R> coCatch(
    error: KFunction1<Throwable, Error>,
    crossinline action: suspend CoroutineScope.() -> R,
): Either<Error, R> =
    coroutineScope {
        catch { action() }
            .mapLeft { error(it) }
    }

fun <L, R> catch(
    error: (Throwable) -> L,
    action: () -> R,
): Either<L, R> =
    catch(action)
        .mapLeft { error(it) }

fun <R> Either<Error, R>.throwIfLeft(): R =
    fold(
        ifLeft = { throw EitherErrorException(it) },
        ifRight = { it },
    )
