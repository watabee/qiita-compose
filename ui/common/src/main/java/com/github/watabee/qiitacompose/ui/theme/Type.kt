package com.github.watabee.qiitacompose.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.watabee.qiitacompose.ui.common.R

object QiitaFontFamily {
    val codecCold = FontFamily(
        Font(resId = R.font.codec_cold_bold, weight = FontWeight.Bold)
    )
}

// Set of Material typography styles to start with
// https://www.figma.com/file/32nOOGSSy4nShyCmEktpjs/Qiita-VI---Color%2C-Text-and-Brand-Assets?node-id=19%3A17
val typography =
    Typography(
        h1 = TextStyle(fontWeight = FontWeight.W700, fontSize = 32.sp, lineHeight = 44.8.sp),
        h2 = TextStyle(fontWeight = FontWeight.W700, fontSize = 24.sp, lineHeight = 33.6.sp),
        subtitle1 = TextStyle(fontWeight = FontWeight.W700, fontSize = 20.sp, lineHeight = 32.sp),
        subtitle2 = TextStyle(fontWeight = FontWeight.W700, fontSize = 18.sp, lineHeight = 28.8.sp),
        body1 = TextStyle(fontWeight = FontWeight.W400, fontSize = 16.sp, lineHeight = 28.8.sp),
        body2 = TextStyle(fontWeight = FontWeight.W400, fontSize = 14.sp, lineHeight = 25.2.sp),
        caption = TextStyle(fontWeight = FontWeight.W400, fontSize = 12.sp, lineHeight = 21.6.sp)
        /* Other default text styles to override
        button = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        ),
        */
    )
