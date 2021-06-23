package com.github.watabee.qiitacompose.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.data.UserData
import com.github.watabee.qiitacompose.ui.items.ItemsScreen
import com.github.watabee.qiitacompose.ui.navigation.AppRouting
import com.github.watabee.qiitacompose.ui.theme.QiitaFontFamily
import com.github.watabee.qiitacompose.ui.util.lifecycleAwareFlow
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun HomeScreen(scaffoldState: ScaffoldState = rememberScaffoldState(), appRouting: AppRouting) {
    val viewModel: HomeViewModel = hiltViewModel()
    val userData: UserData? by viewModel.userData.lifecycleAwareFlow().collectAsState(initial = null)

    HomeScreen(
        scaffoldState = scaffoldState,
        userData = userData,
        appRouting = appRouting
    )
}

@Composable
private fun HomeScreen(
    scaffoldState: ScaffoldState,
    userData: UserData?,
    appRouting: AppRouting
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Qiita", fontFamily = QiitaFontFamily.codecCold, fontWeight = FontWeight.Bold)
                },
                actions = {
                    val onClickAction: () -> Unit = if (userData != null) appRouting.openMyPageScreen else appRouting.openLoginScreen
                    IconButton(onClick = onClickAction) {
                        UserIcon(imageUrl = userData?.imageUrl, iconSize = 24.dp)
                    }
                }
            )
        },
        content = {
            ItemsScreen(appRouting.openUserScreen)
        }
    )
}

@Composable
private fun UserIcon(imageUrl: String?, iconSize: Dp) {
    if (!imageUrl.isNullOrBlank()) {
        Image(
            painter = rememberCoilPainter(
                request = imageUrl,
                requestBuilder = {
                    transformations(CircleCropTransformation())
                }
            ),
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
