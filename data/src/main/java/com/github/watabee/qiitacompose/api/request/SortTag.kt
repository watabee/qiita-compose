package com.github.watabee.qiitacompose.api.request

import java.util.Locale

// https://qiita.com/api/v2/docs#get-apiv2tags
enum class SortTag {
    COUNT,
    NAME,
    ;

    override fun toString(): String {
        return super.toString().lowercase(Locale.US)
    }
}
