package com.library.books.application.service

import io.kotest.core.spec.style.FeatureSpec

class FindBookListServiceSpec : FeatureSpec({
/*
    val repository = mockk<FindBookPageOutPort>()
    val service = FindBookPageService(repository = repository)

    beforeEach { clearAllMocks() }


    feature("find book list by filter") {

        val bookList = listOf(aCreatedBook())

        scenario("filter without exclusion") {
            val filter = BookFilter(id = id)
            val filterExcludingDelete = BookFilter(id = id, exclude = Exclude(status = setOf(DELETED)))

            every { repository.findBy(filterExcludingDelete) } returns bookList.right()

            service.findBy(filter) shouldBeRight bookList

            verify(exactly = 1) { repository.findListBy(filterExcludingDelete) }
        }

        scenario("filter with exclusion") {
            val filter = BookFilter(id = id, exclude = Exclude(status = setOf(CREATED, DELETED)))
            val filterExcludingDelete = BookFilter(id = id, exclude = Exclude(status = setOf(CREATED, DELETED)))

            every { repository.findListBy(filterExcludingDelete) } returns bookList.right()

            service.findBy(filter) shouldBeRight bookList

            verify(exactly = 1) { repository.findListBy(filterExcludingDelete) }
        }

        scenario("error searching book list") {
            val filter = BookFilter(id = id, exclude = Exclude(status = setOf(CREATED, DELETED)))
            val filterExcludingDelete = BookFilter(id = id, exclude = Exclude(status = setOf(CREATED, DELETED)))
            val error = aGenericServerError()

            every { repository.findListBy(filterExcludingDelete) } returns error.left()

            service.findBy(filter) shouldBeLeft error

            verify(exactly = 1) { repository.findListBy(filterExcludingDelete) }
        }


    }
*/
})
