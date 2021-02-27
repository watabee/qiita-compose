package com.github.watabee.qiitacompose.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.util.AppDateFormatter
import java.util.Date

@Composable
internal fun ItemListItem(item: Item) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "@${item.user.id} が${AppDateFormatter.dateToString("yyyy年MM月dd日", item.createdAt)}に投稿",
                    style = MaterialTheme.typography.caption
                )
                Spacer(modifier = Modifier.requiredHeight(4.dp))
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(text = item.title, style = MaterialTheme.typography.h6)
                }
                Spacer(modifier = Modifier.requiredHeight(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_items_tag),
                        contentDescription = null,
                        modifier = Modifier.requiredSize(16.dp)
                    )
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    Text(
                        text = item.tags.joinToString(", ") { it.name },
                        style = MaterialTheme.typography.caption
                    )
                }
                Spacer(modifier = Modifier.requiredHeight(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.requiredSize(16.dp))
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    Text(text = item.likesCount.toString(), style = MaterialTheme.typography.caption)
                }
            }
        }
        Divider()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewItemListItem() {
    ItemListItem(
        item = Item(
            id = "id",
            title = "title",
            createdAt = Date(),
            updatedAt = Date(),
            url = "",
            user = User(
                id = "id",
                name = null,
                description = null,
                organization = null,
                followeesCount = 10,
                followersCount = 10,
                itemsCount = 32,
                profileImageUrl = ""
            ),
            likesCount = 10,
            reactionsCount = 20,
            pageViewsCount = 10,
            commentsCount = 5,
            tags = listOf(Item.Tag("HTML"), Item.Tag("CSS"), Item.Tag("JavaScript"), Item.Tag("JavaScript"), Item.Tag("JavaScript"), Item.Tag("JavaScript"), Item.Tag("JavaScript"))
        )
    )
}
