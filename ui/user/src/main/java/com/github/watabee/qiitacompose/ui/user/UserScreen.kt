package com.github.watabee.qiitacompose.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.common.ErrorScreen
import com.github.watabee.qiitacompose.ui.common.LoadingScreen
import com.github.watabee.qiitacompose.ui.common.SnsIconButtons
import com.github.watabee.qiitacompose.ui.common.UserCountTexts
import com.github.watabee.qiitacompose.ui.navigation.AppRouting
import com.github.watabee.qiitacompose.ui.state.ToastMessageId
import com.github.watabee.qiitacompose.ui.state.show
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.github.watabee.qiitacompose.ui.theme.tagBackground
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsPadding

@Composable
fun UserScreen(userId: String, appRouting: AppRouting, navigateUp: () -> Unit) {

    UserScreen(
        userViewModel = hiltViewModel(),
        userId = userId,
        openLoginScreen = appRouting.openLoginScreen,
        navigateUp = navigateUp
    )
}

@Composable
internal fun UserScreen(userViewModel: UserViewModel, userId: String, openLoginScreen: () -> Unit, navigateUp: () -> Unit) {
    val userUiModel by userViewModel.state.collectAsState(UserUiModel(isLoading = true))

    LaunchedEffect(userViewModel, userId) {
        userViewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId))
    }

    UserScreen(
        userUiModel = userUiModel,
        retryToGetUserInfo = { userViewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId)) },
        openLoginScreen = openLoginScreen,
        followUser = { userViewModel.dispatchAction(UserViewModel.Action.FollowUser(it)) },
        unfollowUser = { userViewModel.dispatchAction(UserViewModel.Action.UnfollowUser(it)) },
        navigateUp = navigateUp,
        onToastMessageShown = { userViewModel.toastMessageShown(it) }
    )
}

@Composable
internal fun UserScreen(
    userUiModel: UserUiModel,
    retryToGetUserInfo: () -> Unit,
    openLoginScreen: () -> Unit,
    followUser: (String) -> Unit,
    unfollowUser: (String) -> Unit,
    navigateUp: () -> Unit,
    onToastMessageShown: (ToastMessageId) -> Unit
) {
    val context = LocalContext.current

    val toastMessage = userUiModel.toastMessages.firstOrNull()
    if (toastMessage != null) {
        LaunchedEffect(toastMessage) {
            toastMessage.show(context)
            onToastMessageShown(toastMessage.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = userUiModel.user?.name.orEmpty())
                }
            )
        },
        content = { paddingValues ->
            UserScreen(
                modifier = Modifier.padding(paddingValues),
                userUiModel = userUiModel,
                retryToGetUserInfo = retryToGetUserInfo,
                openLoginScreen = openLoginScreen,
                followUser = followUser,
                unfollowUser = unfollowUser
            )
        }
    )
}

@Composable
private fun UserScreen(
    modifier: Modifier = Modifier,
    userUiModel: UserUiModel,
    retryToGetUserInfo: () -> Unit,
    openLoginScreen: () -> Unit,
    followUser: (String) -> Unit,
    unfollowUser: (String) -> Unit
) {
    when {
        userUiModel.isLoading -> {
            LoadingScreen(modifier)
        }
        userUiModel.getUserInfoError -> {
            ErrorScreen(modifier, onRetryButtonClicked = { retryToGetUserInfo() })
        }
        userUiModel.user != null -> {
            UserProfileScreen(
                modifier,
                userUiModel.user,
                userUiModel.followButtonState,
                userUiModel.followingTags,
                openLoginScreen,
                followUser,
                unfollowUser
            )
        }
    }
}

@Composable
private fun UserProfileScreen(
    modifier: Modifier = Modifier,
    user: User,
    followButtonState: UserUiModel.FollowButtonState,
    followingTags: List<Tag>,
    openLoginScreen: () -> Unit,
    followUser: (String) -> Unit,
    unfollowUser: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.requiredHeight(28.dp))
        Image(
            painter = rememberImagePainter(
                data = user.profileImageUrl,
                builder = {
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

        Spacer(modifier = Modifier.requiredHeight(24.dp))
        FollowButton(
            userId = user.id,
            state = followButtonState,
            openLoginScreen = openLoginScreen,
            followUser = followUser,
            unfollowUser = unfollowUser
        )

        FollowingTags(followingTags = followingTags)
    }
}

@Composable
private fun FollowButton(
    userId: String,
    state: UserUiModel.FollowButtonState,
    openLoginScreen: () -> Unit,
    followUser: (String) -> Unit,
    unfollowUser: (String) -> Unit
) {
    when (state) {
        UserUiModel.FollowButtonState.PROCESSING -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        UserUiModel.FollowButtonState.FOLLOWING -> {
            Button(
                onClick = { unfollowUser(userId) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp))
            ) {
                Text(
                    text = stringResource(id = R.string.user_now_following),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.W700
                )
            }
        }
        UserUiModel.FollowButtonState.UNFOLLOWING -> {
            OutlinedButton(
                onClick = { followUser(userId) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
            ) {
                Text(text = stringResource(id = R.string.user_follow), style = MaterialTheme.typography.body2, fontWeight = FontWeight.W700)
            }
        }
        UserUiModel.FollowButtonState.LOGIN_REQUIRED -> {
            OutlinedButton(
                onClick = openLoginScreen,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
            ) {
                Text(text = stringResource(id = R.string.user_follow), style = MaterialTheme.typography.body2, fontWeight = FontWeight.W700)
            }
        }
    }
}

@Composable
private fun FollowingTags(followingTags: List<Tag>) {
    if (followingTags.isEmpty()) {
        return
    }

    Spacer(modifier = Modifier.requiredHeight(48.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(painter = painterResource(id = R.drawable.ic_tags), contentDescription = null, tint = MaterialTheme.colors.primary)
        Spacer(modifier = Modifier.requiredWidth(8.dp))
        Text(text = stringResource(id = R.string.user_following_tags), style = MaterialTheme.typography.body2, fontWeight = FontWeight.W700)
    }
    Spacer(modifier = Modifier.requiredHeight(8.dp))
    FlowRow(modifier = Modifier.fillMaxWidth(), mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
        followingTags.forEach { tag ->
            Text(
                text = tag.id,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.tagBackground)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewUserProfileScreen() {
    val user = User(
        id = "increqiita",
        profileImageUrl = "https://qiita-image-store.s3.amazonaws.com/0/190187/profile-images/1499068142",
        description = "Rubyが好きです。Railsも書いています。最近はElixirに興味があります。 Qiitaの開発をしています。",
        facebookId = "facebook",
        followeesCount = 2000,
        followersCount = 230,
        githubLoginName = "github",
        itemsCount = 122,
        linkedinId = "linkedin",
        location = null,
        name = "Innkuri Kiita",
        organization = null,
        twitterScreenName = "twitter",
        websiteUrl = null
    )

    val tags = listOf(
        Tag(id = "Android", itemsCount = 1000, followersCount = 100, iconUrl = ""),
        Tag(id = "iOS", itemsCount = 1000, followersCount = 100, iconUrl = "")
    )

    QiitaTheme {
        UserProfileScreen(
            user = user,
            followButtonState = UserUiModel.FollowButtonState.PROCESSING,
            followingTags = tags,
            openLoginScreen = {},
            followUser = {},
            unfollowUser = {}
        )
    }
}
