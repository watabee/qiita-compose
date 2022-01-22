package com.github.watabee.qiitacompose.ui.user

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.data.UserData
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.state.ToastMessage
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun shutdown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when success to get user info then state is updated`() = runTest {
        val qiitaRepository: QiitaRepository = mockk {
            coEvery { isFollowingUser(any()) } coAnswers {
                delay(300L)
                QiitaApiResult.Success(response = false, rate = null)
            }
            coEvery { getUserFollowingTags(any()) } coAnswers {
                delay(200L)
                QiitaApiResult.Success(response = tags, rate = null)
            }
        }
        val userRepository: UserRepository = mockk {
            coEvery { findById(any()) } coAnswers {
                delay(100L)
                user
            }
        }
        val dataStore: UserDataStore = mockk {
            every { userDataFlow } returns flowOf(UserData(accessToken = "dummy", imageUrl = null))
        }

        val viewModel = UserViewModel(qiitaRepository, userRepository, dataStore)
        viewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId = "dummy"))

        viewModel.state
            .test {
                assertIsLoading()

                advanceTimeBy(300L)

                awaitItem().let {
                    Truth.assertThat(it.isLoading).isFalse()
                    Truth.assertThat(it.getUserInfoError).isFalse()
                    Truth.assertThat(it.user).isEqualTo(user)
                    Truth.assertThat(it.followButtonState).isEqualTo(UserUiModel.FollowButtonState.UNFOLLOWING)
                    Truth.assertThat(it.followingTags).containsExactlyElementsIn(tags)
                    Truth.assertThat(it.toastMessages).isEmpty()
                }

                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `when failed to get user info then state is updated`() = runTest {
        val qiitaRepository: QiitaRepository = mockk {
            coEvery { isFollowingUser(any()) } coAnswers {
                delay(300L)
                QiitaApiResult.Success(response = false, rate = null)
            }
            coEvery { getUserFollowingTags(any()) } coAnswers {
                delay(200L)
                QiitaApiResult.Success(response = tags, rate = null)
            }
        }
        val userRepository: UserRepository = mockk {
            coEvery { findById(any()) } coAnswers {
                delay(100L)
                throw RuntimeException()
            }
        }
        val dataStore: UserDataStore = mockk {
            every { userDataFlow } returns flowOf(UserData(accessToken = "dummy", imageUrl = null))
        }
        val viewModel = UserViewModel(qiitaRepository, userRepository, dataStore)
        viewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId = "dummy"))

        viewModel.state
            .test {
                assertIsLoading()

                advanceTimeBy(100L)

                awaitItem().let {
                    Truth.assertThat(it.isLoading).isFalse()
                    Truth.assertThat(it.getUserInfoError).isTrue()
                }

                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `when success to follow user then state is updated`() = runTest {
        val qiitaRepository: QiitaRepository = mockk {
            coEvery { isFollowingUser(any()) } returns QiitaApiResult.Success(response = false, rate = null)
            coEvery { getUserFollowingTags(any()) } returns QiitaApiResult.Success(response = tags, rate = null)
            coEvery { followUser(any()) } coAnswers {
                delay(100L)
                QiitaApiResult.Success(response = Unit, rate = null)
            }
        }
        val userRepository: UserRepository = mockk {
            coEvery { findById(any()) } returns user
        }
        val dataStore: UserDataStore = mockk {
            every { userDataFlow } returns flowOf(UserData(accessToken = "dummy", imageUrl = null))
        }
        val viewModel = UserViewModel(qiitaRepository, userRepository, dataStore)
        viewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId = "dummy"))

        viewModel.state
            .test {
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.UNFOLLOWING)

                viewModel.dispatchAction(UserViewModel.Action.FollowUser(userId = "dummy"))
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.PROCESSING)
                advanceTimeBy(100L)
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.FOLLOWING)

                cancelAndConsumeRemainingEvents()
            }
    }

    @Test
    fun `when fail to follow user then toast message is shown`() = runTest {
        val qiitaRepository: QiitaRepository = mockk {
            coEvery { isFollowingUser(any()) } returns QiitaApiResult.Success(response = false, rate = null)
            coEvery { getUserFollowingTags(any()) } returns QiitaApiResult.Success(response = tags, rate = null)
            coEvery { followUser(any()) } coAnswers {
                delay(100L)
                QiitaApiResult.Failure.NetworkFailure(IOException())
            }
        }
        val userRepository: UserRepository = mockk {
            coEvery { findById(any()) } returns user
        }
        val dataStore: UserDataStore = mockk {
            every { userDataFlow } returns flowOf(UserData(accessToken = "dummy", imageUrl = null))
        }
        val viewModel = UserViewModel(qiitaRepository, userRepository, dataStore)
        viewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId = "dummy"))

        viewModel.state
            .test {
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.UNFOLLOWING)

                viewModel.dispatchAction(UserViewModel.Action.FollowUser(userId = "dummy"))
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.PROCESSING)
                advanceTimeBy(100L)

                val toastMessageId = with(awaitItem()) {
                    Truth.assertThat(followButtonState).isEqualTo(UserUiModel.FollowButtonState.UNFOLLOWING)
                    Truth.assertThat(toastMessages).hasSize(1)

                    val toastMessage = toastMessages[0]
                    val message = toastMessage.message as? ToastMessage.Message.ResourceMessage
                    Truth.assertThat(message).isNotNull()

                    toastMessage.id
                }

                viewModel.toastMessageShown(toastMessageId)

                Truth.assertThat(awaitItem().toastMessages).isEmpty()

                cancelAndConsumeRemainingEvents()
            }
    }

    @Test
    fun `when success to unfollow user then state is updated`() = runTest {
        val qiitaRepository = mockk<QiitaRepository> {
            coEvery { isFollowingUser(any()) } returns QiitaApiResult.Success(response = true, rate = null)
            coEvery { getUserFollowingTags(any()) } returns QiitaApiResult.Success(response = tags, rate = null)
            coEvery { unfollowUser(any()) } coAnswers {
                delay(100L)
                QiitaApiResult.Success(response = Unit, rate = null)
            }
        }
        val userRepository = mockk<UserRepository> {
            coEvery { findById(any()) } returns user
        }
        val dataStore = mockk<UserDataStore> {
            every { userDataFlow } returns flowOf(UserData(accessToken = "dummy", imageUrl = null))
        }
        val viewModel = UserViewModel(qiitaRepository, userRepository, dataStore)
        viewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId = "dummy"))
        viewModel.state
            .test {
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.FOLLOWING)

                viewModel.dispatchAction(UserViewModel.Action.UnfollowUser(userId = "dummy"))
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.PROCESSING)
                advanceTimeBy(100L)
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.UNFOLLOWING)

                cancelAndConsumeRemainingEvents()
            }
    }

    @Test
    fun `when fail to unfollow user then toast message is shown`() = runTest {
        val qiitaRepository = mockk<QiitaRepository> {
            coEvery { isFollowingUser(any()) } returns QiitaApiResult.Success(response = true, rate = null)
            coEvery { getUserFollowingTags(any()) } returns QiitaApiResult.Success(response = tags, rate = null)
            coEvery { unfollowUser(any()) } coAnswers {
                delay(100L)
                QiitaApiResult.Failure.NetworkFailure(IOException())
            }
        }
        val userRepository = mockk<UserRepository> {
            coEvery { findById(any()) } returns user
        }
        val dataStore = mockk<UserDataStore> {
            every { userDataFlow } returns flowOf(UserData(accessToken = "dummy", imageUrl = null))
        }
        val viewModel = UserViewModel(qiitaRepository, userRepository, dataStore)
        viewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId = "dummy"))
        viewModel.state
            .test {
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.FOLLOWING)

                viewModel.dispatchAction(UserViewModel.Action.UnfollowUser(userId = "dummy"))
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.PROCESSING)
                advanceTimeBy(100L)

                val toastMessageId = with(awaitItem()) {
                    Truth.assertThat(followButtonState).isEqualTo(UserUiModel.FollowButtonState.FOLLOWING)
                    Truth.assertThat(toastMessages).hasSize(1)

                    val toastMessage = toastMessages[0]
                    val message = toastMessage.message as? ToastMessage.Message.ResourceMessage
                    Truth.assertThat(message).isNotNull()

                    toastMessage.id
                }

                viewModel.toastMessageShown(toastMessageId)

                Truth.assertThat(awaitItem().toastMessages).isEmpty()

                cancelAndConsumeRemainingEvents()
            }
    }

    fun `if requesting to follow user then re-requesting is ignored`() = runTest {
        val qiitaRepository = mockk<QiitaRepository> {
            coEvery { isFollowingUser(any()) } returns QiitaApiResult.Success(response = true, rate = null)
            coEvery { getUserFollowingTags(any()) } returns QiitaApiResult.Success(response = tags, rate = null)
            coEvery { followUser(any()) } coAnswers {
                delay(100L)
                QiitaApiResult.Success(response = Unit, rate = null)
            }
        }
        val userRepository = mockk<UserRepository> {
            coEvery { findById(any()) } returns user
        }
        val dataStore = mockk<UserDataStore> {
            every { userDataFlow } returns flowOf(UserData(accessToken = "dummy", imageUrl = null))
        }
        val viewModel = UserViewModel(qiitaRepository, userRepository, dataStore)
        viewModel.dispatchAction(UserViewModel.Action.GetUserInfo(userId = "dummy"))

        viewModel.state
            .test {
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.UNFOLLOWING)

                viewModel.dispatchAction(UserViewModel.Action.FollowUser(userId = "dummy"))
                coVerify(exactly = 1) { qiitaRepository.followUser(any()) }

                advanceTimeBy(50L)
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.PROCESSING)

                viewModel.dispatchAction(UserViewModel.Action.FollowUser(userId = "dummy"))
                expectNoEvents()
                coVerify(exactly = 1) { qiitaRepository.followUser(any()) }

                advanceTimeBy(50L)
                Truth.assertThat(awaitItem().followButtonState).isEqualTo(UserUiModel.FollowButtonState.FOLLOWING)
                coVerify(exactly = 1) { qiitaRepository.followUser(any()) }

                cancelAndConsumeRemainingEvents()
            }
    }
}

private suspend fun FlowTurbine<UserUiModel>.assertIsLoading() {
    val uiModel = awaitItem()
    Truth.assertThat(uiModel.isLoading).isTrue()
    Truth.assertThat(uiModel.getUserInfoError).isFalse()
    Truth.assertThat(uiModel.user).isNull()
    Truth.assertThat(uiModel.followButtonState).isEqualTo(UserUiModel.FollowButtonState.LOGIN_REQUIRED)
    Truth.assertThat(uiModel.followingTags).isEmpty()
    Truth.assertThat(uiModel.toastMessages).isEmpty()
}

private val user = User(
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

private val tags: List<Tag> = listOf(
    Tag(id = "Android", itemsCount = 1000, followersCount = 100, iconUrl = ""),
    Tag(id = "iOS", itemsCount = 1000, followersCount = 100, iconUrl = "")
)
