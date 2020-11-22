package com.github.watabee.qiitacompose.api.response

import com.google.common.truth.Truth
import okhttp3.Headers.Companion.toHeaders
import org.junit.Test

class SuccessResponseWithPaginationTest {

    @Test
    fun testWithNextPage() {
        val headers =
            mapOf(
                "rate-limit" to "60",
                "rate-remaining" to "54",
                "rate-reset" to "1605770634",
                "link" to
                    "<https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"first\", <https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"prev\", <https://qiita.com/api/v2/items?page=3&per_page=10&query=Android>; rel=\"next\", <https://qiita.com/api/v2/items?page=2946&per_page=10&query=Android>; rel=\"last\"",
                "total-count" to "29455"
            )
                .toHeaders()

        val response = SuccessResponseWithPagination.create(headers, Unit)
        val rate = response.rate

        Truth.assertThat(rate?.rateLimit).isEqualTo(60)
        Truth.assertThat(rate?.rateRemaining).isEqualTo(54)
        Truth.assertThat(rate?.rateReset?.time).isEqualTo(1605770634000L)
        Truth.assertThat(response.totalCount).isEqualTo(29455)
        Truth.assertThat(response.nextPage).isEqualTo(3)
    }

    @Test
    fun testWithoutNextPage() {
        val headers =
            mapOf(
                "rate-limit" to "60",
                "rate-remaining" to "54",
                "rate-reset" to "1605770634",
                "link" to
                    "<https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"first\", <https://qiita.com/api/v2/items?page=1&per_page=10&query=Android>; rel=\"prev\", <https://qiita.com/api/v2/items?page=2946&per_page=10&query=Android>; rel=\"last\"",
                "total-count" to "29455"
            )
                .toHeaders()

        val response = SuccessResponseWithPagination.create(headers, Unit)
        val rate = response.rate

        Truth.assertThat(rate?.rateLimit).isEqualTo(60)
        Truth.assertThat(rate?.rateRemaining).isEqualTo(54)
        Truth.assertThat(rate?.rateReset?.time).isEqualTo(1605770634000L)
        Truth.assertThat(response.totalCount).isEqualTo(29455)
        Truth.assertThat(response.nextPage).isEqualTo(null)
    }
}
