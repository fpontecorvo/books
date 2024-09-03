package com.library.books.shared.config

import com.library.books.book.adapter.inport.controller.model.BookFilterParams
import com.library.books.book.adapter.inport.controller.model.BookRequest
import com.library.books.book.adapter.inport.controller.model.BookResponse
import com.library.books.book.adapter.inport.error.ApiErrorResponse
import com.library.books.shared.adapter.inport.controller.model.PageResponse
import org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
import org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS
import org.springframework.aot.hint.MemberCategory.PUBLIC_FIELDS
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import java.lang.Enum.EnumDesc

@Configuration
@RegisterReflectionForBinding(
    BookRequest::class,
    BookResponse::class,
    BookFilterParams::class,
    ApiErrorResponse::class,
    ApiErrorResponse.ApiError::class,
    EnumDesc::class,
    PageResponse::class,
    PageResponse.Metadata::class,
)
@ImportRuntimeHints(RuntimeReflectionConfig.AppRuntimeHintsRegistrar::class)
internal class RuntimeReflectionConfig {
    class AppRuntimeHintsRegistrar : RuntimeHintsRegistrar {
        override fun registerHints(
            hints: RuntimeHints,
            classLoader: ClassLoader?,
        ) {
            hints.reflection()
                .registerType(
                    BookRequest::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                ).registerType(
                    BookResponse::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                ).registerType(
                    BookFilterParams::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                ).registerType(
                    ApiErrorResponse::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                ).registerType(
                    ApiErrorResponse.ApiError::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                ).registerType(
                    EnumDesc::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                ).registerType(
                    PageResponse::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                ).registerType(
                    PageResponse.Metadata::class.java,
                    PUBLIC_FIELDS,
                    INVOKE_PUBLIC_METHODS,
                    INVOKE_PUBLIC_CONSTRUCTORS,
                )
        }
    }
}
