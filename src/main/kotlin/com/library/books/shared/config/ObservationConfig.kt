package com.library.books.shared.config

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter

@Configuration
class ObservationConfig(private val observationRegistry: ObservationRegistry) {
    @Bean
    fun coroutineWebFilter(): WebFilter {
        return object : CoWebFilter() {
            override suspend fun filter(
                exchange: ServerWebExchange,
                chain: CoWebFilterChain,
            ) = withContext(observationRegistry.asContextElement()) {
                chain.filter(exchange)
            }
        }
    }
}
