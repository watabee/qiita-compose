package com.github.watabee.qiitacompose.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.ui.items.ItemsScreen
import com.github.watabee.qiitacompose.ui.theme.QiitaFontFamily
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(scaffoldState: ScaffoldState = rememberScaffoldState()) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } })
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
            ItemsScreen()
        }
    )
}

@Composable
private fun AppDrawer(closeDrawer: () -> Unit) {
    val viewModel: HomeViewModel = viewModel()
    val isLoggedIn: Boolean by viewModel.isLoggedIn.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoggedIn) {
            val authenticatedUserState: GetAuthenticatedUserState by viewModel.authenticatedUserState.collectAsState()
            UserInformation(authenticatedUserState = authenticatedUserState)
        } else {
            DrawerButton(icon = Icons.Filled.Home, label = "Login", isSelected = false, action = { /*TODO*/ })
        }
        Spacer(Modifier.requiredHeight(24.dp))
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = "Home",
            isSelected = true,
            action = {
                closeDrawer()
            }
        )
    }
}

@Composable
private fun UserInformation(authenticatedUserState: GetAuthenticatedUserState) {
    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
        when (authenticatedUserState) {
            is GetAuthenticatedUserState.Success -> {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                }
            }
            GetAuthenticatedUserState.Error -> {
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
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colors
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }
    val textIconColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()

    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = null, // decorative
                    colorFilter = ColorFilter.tint(textIconColor),
                    alpha = imageAlpha
                )
                Spacer(Modifier.requiredWidth(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor
                )
            }
        }
    }
}
