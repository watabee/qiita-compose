package com.github.watabee.qiitacompose.api.response

class SuccessResponse<out T : Any>(val rate: Rate?, val response: T)
