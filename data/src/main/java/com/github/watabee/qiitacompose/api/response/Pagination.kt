package com.github.watabee.qiitacompose.api.response

import okhttp3.Headers

data class Pagination(val totalCount: Int, val firstPage: Int, val prevPage: Int?, val nextPage: Int?, val lastPage: Int) {

    internal companion object {
        private const val FIRST = "first"
        private const val PREV = "prev"
        private const val NEXT = "next"
        private const val LAST = "last"
        private val LINK_REGEX = """^<.+[?&]page=(\d+).*>; rel="($FIRST|$PREV|$NEXT|$LAST)"$""".toRegex()

        operator fun invoke(headers: Headers): Pagination? {
            val totalCount = headers["total-count"]?.toIntOrNull()
            val pages = parseLinkHeader(headers["link"])

            if (totalCount == null || pages == null) {
                return null
            }

            println("pages = $pages")
            return Pagination(
                totalCount = totalCount,
                firstPage = pages[FIRST]!!,
                prevPage = pages[PREV],
                nextPage = pages[NEXT],
                lastPage = pages[LAST]!!,
            )
        }

        private fun parseLinkHeader(linkHeaderValue: String?): Map<String, Int?>? {
            val links = linkHeaderValue?.split(",")
            if (links.isNullOrEmpty()) {
                return null
            }
            return links.mapNotNull { link -> LINK_REGEX.matchEntire(link.trim())?.groupValues?.run { this[2] to this[1].toIntOrNull() } }
                .toMap()
        }
    }
}
