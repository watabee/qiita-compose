package com.github.watabee.qiitacompose.api.response

import com.google.common.truth.Truth
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PaginationTest(private val headers: Headers, private val expectedPagination: Pagination?) {

    @Test
    fun test() {
        val pagination = Pagination(headers)
        Truth.assertThat(pagination).isEqualTo(expectedPagination)
    }

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun data() = listOf(
            arrayOf(
                mapOf(
                    "total-count" to "29455",
                    "link" to "<https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"first\", <https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"prev\", <https://qiita.com/api/v2/items?page=3&per_page=10&query=Android>; rel=\"next\", <https://qiita.com/api/v2/items?page=2946&per_page=10&query=Android>; rel=\"last\""
                ).toHeaders(),
                Pagination(totalCount = 29455, firstPage = 1, prevPage = 1, nextPage = 3, lastPage = 2946)
            ),
            arrayOf(
                mapOf(
                    "total-count" to "29455",
                    "link" to "<https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"first\", <https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"prev\", <https://qiita.com/api/v2/items?page=2946&per_page=10&query=Android>; rel=\"last\""
                ).toHeaders(),
                Pagination(totalCount = 29455, firstPage = 1, prevPage = 1, nextPage = null, lastPage = 2946)
            ),
            arrayOf(
                mapOf("total-count" to "29455").toHeaders(),
                null
            ),
            arrayOf(
                mapOf(
                    "link" to "<https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"first\", <https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"prev\", <https://qiita.com/api/v2/items?page=2946&per_page=10&query=Android>; rel=\"last\""
                ).toHeaders(),
                null
            ),
            arrayOf(
                emptyMap<String, String>().toHeaders(),
                null
            )
        )
    }
}
