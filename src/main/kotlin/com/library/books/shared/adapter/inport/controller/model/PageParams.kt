package com.library.books.shared.adapter.inport.controller.model

import com.library.books.shared.domain.PageRequirement

data class PageParams(
    val size: Int = 10,
    val page: Int = 0,
) {
    fun toRequirement() =
        PageRequirement(
            size = size,
            page = page,
        )
}
