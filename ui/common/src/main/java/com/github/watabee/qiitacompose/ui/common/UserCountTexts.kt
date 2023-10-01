package com.github.watabee.qiitacompose.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme

@Composable
fun UserCountTexts(itemsCount: Int, followeesCount: Int, followersCount: Int) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            val (itemsText, followeesText, followersText) = createRefs()
            Text(
                text = stringResource(id = R.string.common_user_items_count, itemsCount),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.constrainAs(itemsText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(followeesText.start)
                },
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = stringResource(id = R.string.common_user_followees_count, followeesCount),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.constrainAs(followeesText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = stringResource(id = R.string.common_user_followers_count, followersCount),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.constrainAs(followersText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(followeesText.end)
                    end.linkTo(parent.end)
                },
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
@Preview(name = "UserCountTexts", showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewUserCountTexts() {
    QiitaTheme {
        UserCountTexts(itemsCount = 100, followeesCount = 20, followersCount = 30)
    }
}
