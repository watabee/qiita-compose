package com.github.watabee.qiitacompose.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.ui.common.AppOutlinedButton
import com.github.watabee.qiitacompose.ui.common.navViewModel
import com.github.watabee.qiitacompose.ui.items.ItemsRouting
import com.github.watabee.qiitacompose.ui.items.ItemsScreen
import com.github.watabee.qiitacompose.ui.theme.QiitaFontFamily
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.google.accompanist.coil.CoilImage
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(scaffoldState: ScaffoldState = rememberScaffoldState(), homeRouting: HomeRouting, itemsRouting: ItemsRouting) {
    val coroutineScope = rememberCoroutineScope()
    var isVisibleLogoutConfirmationDialog by remember { mutableStateOf(false) }
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                homeRouting = homeRouting,
                closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } },
                onLogoutButtonClicked = { isVisibleLogoutConfirmationDialog = true }
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Qiita", fontFamily = QiitaFontFamily.codecCold, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                    }
                }
            )
        },
        content = {
            ItemsScreen(itemsRouting)
            if (isVisibleLogoutConfirmationDialog) {
                LogoutConfirmationDialog(onDismissRequest = { isVisibleLogoutConfirmationDialog = false })
            }
        }
    )
}

@Composable
private fun AppDrawer(
    homeRouting: HomeRouting,
    closeDrawer: () -> Unit,
    onLogoutButtonClicked: () -> Unit
) {
    val viewModel: HomeViewModel = navViewModel()
    val isLoggedIn: Boolean by viewModel.isLoggedIn.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        DrawerHeader(
            homeRouting = homeRouting,
            isLoggedIn = isLoggedIn,
            onLogoutButtonClicked = onLogoutButtonClicked
        )
        Spacer(Modifier.requiredHeight(24.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        Spacer(modifier = Modifier.requiredHeight(16.dp))
        Text(text = "記事フィード", style = MaterialTheme.typography.caption, fontWeight = FontWeight.W700)
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        DrawerButton(
            icon = painterResource(id = R.drawable.ic_home_home),
            label = stringResource(id = R.string.home_drawer_menu_home),
            isSelected = true,
            action = {
                closeDrawer()
            }
        )
    }
}

@Composable
private fun DrawerHeader(
    homeRouting: HomeRouting,
    isLoggedIn: Boolean,
    onLogoutButtonClicked: () -> Unit
) {
    val viewModel: HomeViewModel = navViewModel()
    Box(modifier = Modifier.padding(top = 16.dp)) {
        if (isLoggedIn) {
            val authenticatedUserState: GetAuthenticatedUserState by viewModel.authenticatedUserState.collectAsState()
            UserInformation(
                authenticatedUserState = authenticatedUserState,
                onLogoutButtonClicked = onLogoutButtonClicked,
                openUserScreen = { }
            )
        } else {
            AppOutlinedButton(onClick = homeRouting.openLoginScreen) {
                Text(text = stringResource(id = R.string.home_login), style = MaterialTheme.typography.button)
            }
        }
    }
}

@Composable
private fun UserInformation(
    authenticatedUserState: GetAuthenticatedUserState,
    onLogoutButtonClicked: () -> Unit,
    openUserScreen: () -> Unit
) {
    when (authenticatedUserState) {
        is GetAuthenticatedUserState.Success -> {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openUserScreen() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoilImage(
                        data = authenticatedUserState.user.profileImageUrl,
                        contentDescription = null,
                        modifier = Modifier.requiredSize(32.dp),
                        requestBuilder = {
                            transformations(CircleCropTransformation())
                        }
                    )
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    Text(text = authenticatedUserState.user.id, style = MaterialTheme.typography.subtitle2)
                }

                Spacer(modifier = Modifier.requiredHeight(16.dp))

                AppOutlinedButton(onClick = onLogoutButtonClicked) {
                    Text(text = stringResource(id = R.string.home_logout), style = MaterialTheme.typography.button)
                }
            }
        }
        GetAuthenticatedUserState.Error -> {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = null,
                            modifier = Modifier.requiredSize(32.dp),
                            tint = MaterialTheme.colors.error
                        )
                        Spacer(modifier = Modifier.requiredWidth(8.dp))
                        Text(
                            text = stringResource(id = R.string.home_failed_to_get_user_information),
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                }
                Spacer(modifier = Modifier.requiredHeight(16.dp))

                val homeViewModel: HomeViewModel = navViewModel()
                AppOutlinedButton(
                    onClick = { homeViewModel.retryToGetAuthenticatedUser() }
                ) {
                    Text(text = stringResource(id = R.string.common_retry), style = MaterialTheme.typography.button)
                }
            }
        }
        GetAuthenticatedUserState.Loading -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.requiredSize(32.dp))
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    Text(text = stringResource(id = R.string.home_getting_user_information), style = MaterialTheme.typography.subtitle2)
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    CircularProgressIndicator(modifier = Modifier.requiredSize(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun DrawerButton(
    icon: Painter,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (isSelected) ContentAlpha.high else ContentAlpha.medium
    val backgroundColor = if (isSelected) colorResource(id = R.color.green_40) else Color.Transparent

    TextButton(
        onClick = action,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor, contentColor = MaterialTheme.colors.onBackground),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompositionLocalProvider(LocalContentAlpha provides alpha) {
                Icon(painter = icon, contentDescription = null)
                Spacer(Modifier.requiredWidth(8.dp))
                Text(text = label, style = MaterialTheme.typography.caption, fontWeight = FontWeight.W700)
            }
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(onDismissRequest: () -> Unit) {
    val viewModel: HomeViewModel = navViewModel()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Text(text = stringResource(id = R.string.home_confirm_to_logout), style = MaterialTheme.typography.subtitle1)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.logout()
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(id = R.string.common_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.common_no))
            }
        }
    )
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun UserInformationPreview() {
    val authenticatedUser = AuthenticatedUser(
        id = "watabee",
        profileImageUrl = "https://qiita-image-store.s3.amazonaws.com/0/190187/profile-images/1499068142",
        description = null,
        facebookId = null,
        followeesCount = 0,
        followersCount = 0,
        githubLoginName = null,
        imageMonthlyUploadLimit = 0,
        imageMonthlyUploadRemaining = 0,
        itemsCount = 0,
        linkedinId = null,
        location = null,
        name = null,
        organization = null,
        permanentId = 1,
        teamOnly = false,
        twitterScreenName = null,
        websiteUrl = null
    )
    QiitaTheme {
        UserInformation(
            authenticatedUserState = GetAuthenticatedUserState.Success(authenticatedUser),
            onLogoutButtonClicked = {},
            openUserScreen = {}
        )
    }
}
