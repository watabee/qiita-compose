package com.github.watabee.qiitacompose.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.state.ToastMessage
import com.github.watabee.qiitacompose.ui.state.ToastMessageId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    private val userRepository: UserRepository,
    dataStore: UserDataStore,
) : ViewModel() {

    private val isLoggedIn: StateFlow<Boolean> = dataStore.userDataFlow
        .map { !it?.accessToken.isNullOrBlank() }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    private val _state = MutableStateFlow(UserUiModel())
    val state: StateFlow<UserUiModel> = combine(_state, isLoggedIn) { state, isLoggedIn ->
        if (isLoggedIn) state else state.copy(followButtonState = UserUiModel.FollowButtonState.LOGIN_REQUIRED)
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = UserUiModel())

    private var actionJob: Job? = null

    fun dispatchAction(action: Action) {
        if (actionJob?.isActive == true) {
            return
        }
        actionJob = viewModelScope.launch {
            when (action) {
                is Action.GetUserInfo -> {
                    getUserInfo(action.userId)
                }
                is Action.FollowUser -> {
                    followUser(action.userId)
                }
                is Action.UnfollowUser -> {
                    unfollowUser(action.userId)
                }
            }
        }
    }

    fun toastMessageShown(toastMessageId: ToastMessageId) {
        _state.update { userUiModel ->
            userUiModel.copy(toastMessages = userUiModel.toastMessages.filterNot { it.id == toastMessageId })
        }
    }

    private suspend fun getUserInfo(userId: String) {
        try {
            supervisorScope {
                _state.emit(UserUiModel(isLoading = true))
                val (user, isFollowingUserResult, getUserFollowingTagsResult) = awaitAll(
                    async { userRepository.findById(userId) },
                    async { qiitaRepository.isFollowingUser(userId) },
                    async { qiitaRepository.getUserFollowingTags(userId) },
                )
                @Suppress("UNCHECKED_CAST")
                if (user is User && getUserFollowingTagsResult is QiitaApiResult.Success<*>) {
                    val isFollowingUser = (isFollowingUserResult as? QiitaApiResult.Success<*>)?.response as? Boolean ?: false
                    val followingTags = getUserFollowingTagsResult.response as List<Tag>
                    val followButtonState =
                        if (isFollowingUser) UserUiModel.FollowButtonState.FOLLOWING else UserUiModel.FollowButtonState.UNFOLLOWING
                    _state.update {
                        UserUiModel(user = user, followButtonState = followButtonState, followingTags = followingTags)
                    }
                } else {
                    _state.update { it.copy(isLoading = false, getUserInfoError = true) }
                }
            }
        } catch (e: Throwable) {
            _state.update { it.copy(isLoading = false, getUserInfoError = true) }
        }
    }

    private suspend fun followUser(userId: String) {
        _state.update { uiModel -> uiModel.copy(followButtonState = UserUiModel.FollowButtonState.PROCESSING) }
        when (qiitaRepository.followUser(userId)) {
            is QiitaApiResult.Success -> {
                _state.update { uiModel ->
                    val updatedUser = uiModel.user?.let { user -> user.copy(followersCount = user.followersCount + 1) }
                    uiModel.copy(user = updatedUser, followButtonState = UserUiModel.FollowButtonState.FOLLOWING)
                }
            }
            is QiitaApiResult.Failure -> {
                _state.update { uiModel ->
                    val newToastMessage = ToastMessage(messageResId = R.string.user_follow_user_error_message)
                    uiModel.copy(
                        toastMessages = uiModel.toastMessages + newToastMessage,
                        followButtonState = UserUiModel.FollowButtonState.UNFOLLOWING,
                    )
                }
            }
        }
    }

    private suspend fun unfollowUser(userId: String) {
        _state.update { uiModel -> uiModel.copy(followButtonState = UserUiModel.FollowButtonState.PROCESSING) }
        when (qiitaRepository.unfollowUser(userId)) {
            is QiitaApiResult.Success -> {
                _state.update { uiModel ->
                    val updatedUser = uiModel.user?.let { user -> user.copy(followersCount = user.followersCount - 1) }
                    uiModel.copy(user = updatedUser, followButtonState = UserUiModel.FollowButtonState.UNFOLLOWING)
                }
            }
            is QiitaApiResult.Failure -> {
                _state.update {
                    val newToastMessage = ToastMessage(messageResId = R.string.user_unfollow_user_error_message)
                    it.copy(toastMessages = it.toastMessages + newToastMessage, followButtonState = UserUiModel.FollowButtonState.FOLLOWING)
                }
            }
        }
    }

    sealed class Action {
        data class GetUserInfo(val userId: String) : Action()

        data class FollowUser(val userId: String) : Action()

        data class UnfollowUser(val userId: String) : Action()
    }
}
