package com.github.watabee.qiitacompose.api

import com.github.watabee.qiitacompose.api.response.ErrorResponse
import com.slack.eithernet.statusCode
import java.lang.reflect.Type
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

internal object QiitaErrorResponseConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type, annotations: Array<out Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, ErrorResponse>? {

        if (type != ErrorResponse::class.java) {
            return null
        }

        val (_, nextAnnotations) = annotations.statusCode() ?: return null

        // Delegate to MoshiConverter.
        return retrofit.nextResponseBodyConverter(this, type, nextAnnotations)
    }
}
