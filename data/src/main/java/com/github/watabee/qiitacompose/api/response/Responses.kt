package com.github.watabee.qiitacompose.api.response

import okhttp3.Headers

class SuccessResponse<out T : Any>(val rate: Rate?, val response: T)

class SuccessResponseWithPagination<out T : Any>(
    val rate: Rate?,
    val totalCount: Int,
    val nextPage: Int?,
    val response: T
) {
    companion object {
        private val LINK_REGEX = """<.+[?&]page=(\d+).*>""".toRegex()

        fun <T : Any> create(headers: Headers, response: T): SuccessResponseWithPagination<T> {
            val rate = Rate.parseHeaders(headers)
            val totalCount = headers["total-count"]?.toIntOrNull() ?: 0
            val link = headers["link"].orEmpty()
            val nextPage = parseLink(link)

            return SuccessResponseWithPagination(rate, totalCount, nextPage, response)
        }

        private fun parseLink(link: String): Int? {
            return link
                .split(",")
                .map { it.split(";").map { it.trim() }.run { this[0] to this[1] } }
                .firstOrNull { (_, rel) -> rel == "rel=\"next\"" }
                ?.let { (link, _) ->
                    LINK_REGEX.matchEntire(link)?.groupValues?.getOrNull(1)?.toIntOrNull()
                }
        }
    }
}

data class ErrorResponse(val rate: Rate?, val error: Error)
