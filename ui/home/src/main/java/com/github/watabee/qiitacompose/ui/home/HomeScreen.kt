package com.github.watabee.qiitacompose.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.data.UserData
import com.github.watabee.qiitacompose.ui.items.ItemsScreen
import com.github.watabee.qiitacompose.ui.navigation.AppRouting
import com.github.watabee.qiitacompose.ui.theme.QiitaFontFamily

@Composable
fun HomeScreen(appRouting: AppRouting) {
    val viewModel: HomeViewModel = hiltViewModel()
    val userData: UserData? by viewModel.userData.collectAsState(initial = null)

    HomeScreen(
        userData = userData,
        appRouting = appRouting
    )
}

@Composable
private fun HomeScreen(
    userData: UserData?,
    appRouting: AppRouting
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Qiita", fontFamily = QiitaFontFamily.codecCold, fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { appRouting.openSearchScreen() }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = null)
                    }

                    val onClickAction: () -> Unit = if (userData != null) appRouting.openMyPageScreen else appRouting.openLoginScreen
                    IconButton(onClick = onClickAction) {
                        UserIcon(imageUrl = userData?.imageUrl, iconSize = 24.dp)
                    }
                }
            )
        },
        content = { paddingValues ->
            ItemsScreen(modifier = Modifier.padding(paddingValues), appRouting.openUserScreen, appRouting.openItemDetailScreen)
        }
    )
}

@Composable
private fun UserIcon(imageUrl: String?, iconSize: Dp) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
    } else {
        Icon(
            painter = painterResource(id = R.drawable.ic_blank_user),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
    }
}
