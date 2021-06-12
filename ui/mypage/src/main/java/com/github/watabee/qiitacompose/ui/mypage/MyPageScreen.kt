package com.github.watabee.qiitacompose.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.ui.common.AppAlertDialog
import com.github.watabee.qiitacompose.ui.common.AppOutlinedButton
import com.github.watabee.qiitacompose.ui.common.ErrorScreen
import com.github.watabee.qiitacompose.ui.common.LoadingScreen
import com.github.watabee.qiitacompose.ui.common.SnsIconButtons
import com.github.watabee.qiitacompose.ui.common.UserCountTexts
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.flow.collect

@Composable
fun MyPageScreen(closeMyPageScreen: () -> Unit) {
    val viewModel: MyPageViewModel = hiltViewModel()
    val state: MyPageViewModel.State by viewModel.state.collectAsState()
    val event = viewModel.event
    val (isVisibleLogoutDialog, setVisibleLogoutDialog) = remember { mutableStateOf(false) }
    val closeMyPageScreenState by rememberUpdatedState(closeMyPageScreen)

    LaunchedEffect(event) {
        event.collect { event ->
            when (event) {
                MyPageViewModel.Event.CompletedLogout -> {
                    closeMyPageScreenState()
                }
                MyPageViewModel.Event.EmptyAccessToken -> { /* TODO */ }
            }
        }
    }

    MyPageScreen(
        state = state,
        isVisibleLogoutDialog = isVisibleLogoutDialog,
        setVisibleLogoutDialog = setVisibleLogoutDialog,
        logout = { viewModel.dispatchAction(MyPageViewModel.Action.Logout) },
        getAuthenticatedUser = { viewModel.dispatchAction(MyPageViewModel.Action.GetAuthenticatedUser) }
    )
}

@Composable
private fun MyPageScreen(
    state: MyPageViewModel.State,
    isVisibleLogoutDialog: Boolean,
    setVisibleLogoutDialog: (Boolean) -> Unit,
    logout: () -> Unit,
    getAuthenticatedUser: () -> Unit
) {
    val getAuthenticatedUserState by rememberUpdatedState(getAuthenticatedUser)

    LaunchedEffect(state.authenticatedUser) {
        if (state.authenticatedUser == null) {
            getAuthenticatedUserState()
        }
    }

    when {
        state.isLoading -> {
            LoadingScreen()
        }
        state.isError -> {
            ErrorScreen { getAuthenticatedUser() }
        }
        state.authenticatedUser != null -> {
            MyPageScreen(user = state.authenticatedUser, onLogoutButtonClicked = { setVisibleLogoutDialog(true) })
            LogoutDialog(isVisible = isVisibleLogoutDialog, setVisible = setVisibleLogoutDialog, requestToLogout = logout)
        }
    }
}

@Composable
private fun MyPageScreen(user: AuthenticatedUser, onLogoutButtonClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.requiredHeight(28.dp))
        Image(
            painter = rememberCoilPainter(
                request = user.profileImageUrl,
                requestBuilder = {
                    transformations(CircleCropTransformation())
                }
            ),
            contentDescription = null,
            modifier = Modifier.requiredSize(72.dp),
        )
        Spacer(modifier = Modifier.requiredHeight(16.dp))

        val username = user.name
        if (!username.isNullOrBlank()) {
            Text(text = username, fontWeight = FontWeight.W700, style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.requiredHeight(4.dp))
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(text = "@${user.id}", style = MaterialTheme.typography.body1)
        }
        SnsIconButtons(
            githubLoginName = user.githubLoginName,
            twitterScreenName = user.twitterScreenName,
            facebookId = user.facebookId,
            linkedinId = user.linkedinId
        )
        Spacer(modifier = Modifier.requiredHeight(16.dp))
        Divider(modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        UserCountTexts(itemsCount = user.itemsCount, followeesCount = user.followeesCount, followersCount = user.followersCount)

        val description = user.description
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                lineHeight = 24.sp,
                modifier = Modifier.padding(top = 24.dp),
                fontWeight = FontWeight.W400

            )
        }

        Spacer(modifier = Modifier.requiredHeight(32.dp))
        AppOutlinedButton(
            onClick = onLogoutButtonClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.mypage_logout), style = MaterialTheme.typography.button)
        }
    }
}

@Composable
private fun LogoutDialog(isVisible: Boolean, setVisible: (Boolean) -> Unit, requestToLogout: () -> Unit) {
    if (isVisible) {
        AppAlertDialog(
            text = stringResource(id = R.string.mypage_confirm_to_logout),
            confirmButtonText = stringResource(id = R.string.common_yes),
            onConfirmButtonClicked = {
                requestToLogout()
                setVisible(false)
            },
            dismissButtonText = stringResource(id = R.string.common_no),
            onDismissButtonClicked = { setVisible(false) },
            onDismissRequest = { setVisible(false) }
        )
    }
}
