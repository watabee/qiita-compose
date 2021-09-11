package com.github.watabee.qiitacompose.ui.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.api.response.Item
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.github.watabee.qiitacompose.ui.util.AppDateFormatter
import kotlinx.coroutines.launch
import java.util.Date

@Composable
internal fun ItemListItem(item: Item, openUserScreen: suspend (User) -> Unit) {
    val scope = rememberCoroutineScope()
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberImagePainter(
                            data = item.user.profileImageUrl,
                            builder = {
                                transformations(CircleCropTransformation())
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.requiredSize(20.dp),
                    )
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    TextButton(
                        onClick = {
                            scope.launch { openUserScreen(item.user) }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.high)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "@${item.user.id}", style = MaterialTheme.typography.body2)
                    }
                    Spacer(modifier = Modifier.requiredWidth(4.dp))
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = "が${AppDateFormatter.dateToString("yyyy年MM月dd日", item.createdAt)}に投稿",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
                Spacer(modifier = Modifier.requiredHeight(4.dp))
                Text(text = item.title, style = MaterialTheme.typography.subtitle2)
                Spacer(modifier = Modifier.requiredHeight(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tags),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    Text(
                        text = item.tags.joinToString(", ") { it.name },
                        style = MaterialTheme.typography.body2
                    )
                }
                Spacer(modifier = Modifier.requiredHeight(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_items_lgtm),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    Text(text = item.likesCount.toString(), style = MaterialTheme.typography.body2)
                }
            }
        }
        Divider()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewItemListItem() {
    QiitaTheme {
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
                    profileImageUrl = "",
                    location = null,
                    facebookId = null,
                    githubLoginName = null,
                    twitterScreenName = null,
                    websiteUrl = null,
                    linkedinId = null
                ),
                likesCount = 10,
                reactionsCount = 20,
                pageViewsCount = 10,
                commentsCount = 5,
                tags = listOf(
                    Item.Tag("HTML"),
                    Item.Tag("CSS"),
                    Item.Tag("JavaScript"),
                    Item.Tag("JavaScript"),
                    Item.Tag("JavaScript"),
                    Item.Tag("JavaScript"),
                    Item.Tag("JavaScript")
                )
            ),
            openUserScreen = {}
        )
    }
}
