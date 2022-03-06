package com.github.watabee.qiitacompose.ui.login

import androidx.compose.material.SnackbarDuration
import app.cash.turbine.test
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.AccessTokens
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.api.response.Error
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.ui.util.SnackbarManager
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @MockK private lateinit var userDataStore: UserDataStore
    @MockK private lateinit var snackbarManager: SnackbarManager

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getAccessTokens and getAuthenticatedUser return success then show success snackbar message`() = runTest {
        val accessTokens = AccessTokens(clientId = "client_id", scopes = emptyList(), token = "token")

        val qiitaRepository: QiitaRepository = mockk {
            coEvery { getAccessTokens(any()) } returns QiitaApiResult.Success(response = accessTokens, rate = null, pagination = null)
            coEvery { getAuthenticatedUser(any()) } coAnswers {
                delay(500L)
                QiitaApiResult.Success(
                    response = authenticatedUser,
                    rate = null,
                    pagination = null
                )
            }
        }

        val viewModel = LoginViewModel(qiitaRepository, userDataStore, snackbarManager)
        viewModel.dispatchAction(LoginAction.RequestAccessTokens("code"))

        viewModel.uiState
            .test {
                with(awaitItem()) {
                    Truth.assertThat(screenContent).isEqualTo(LoginUiState.ScreenContent.LOADING)
                    verify(exactly = 0) { snackbarManager.showMessage(any(), any(), any()) }
                }
                with(awaitItem()) {
                    Truth.assertThat(screenContent).isEqualTo(LoginUiState.ScreenContent.EMPTY)
                    verify(exactly = 1) { snackbarManager.showMessage(R.string.login_success_login, SnackbarDuration.Indefinite, any()) }
                }
                cancelAndIgnoreRemainingEvents()
            }

        coVerify(exactly = 1) { qiitaRepository.getAccessTokens("code") }
        coVerify(exactly = 1) { qiitaRepository.getAuthenticatedUser(accessToken = "token") }
        coVerify(exactly = 1) { userDataStore.updateUserData(accessToken = "token", userImageUrl = authenticatedUser.profileImageUrl) }
    }

    @Test
    fun `when getAccessTokens returns failure then show failure snackbar message`() = runTest {
        val qiitaRepository: QiitaRepository = mockk {
            coEvery { getAccessTokens(any()) } coAnswers {
                delay(500L)
                QiitaApiResult.Failure.HttpFailure(
                    statusCode = 403,
                    error = Error(message = "Forbidden", type = "forbidden"),
                    rate = null
                )
            }
        }

        val viewModel = LoginViewModel(qiitaRepository, userDataStore, snackbarManager)
        viewModel.dispatchAction(LoginAction.RequestAccessTokens("code"))

        viewModel.uiState
            .test {
                with(awaitItem()) {
                    Truth.assertThat(screenContent).isEqualTo(LoginUiState.ScreenContent.LOADING)
                    verify(exactly = 0) { snackbarManager.showMessage(any(), any(), any()) }
                }
                with(awaitItem()) {
                    Truth.assertThat(screenContent).isEqualTo(LoginUiState.ScreenContent.EMPTY)
                    verify(exactly = 1) { snackbarManager.showMessage(R.string.login_failure_login, SnackbarDuration.Indefinite, any()) }
                }

                cancelAndIgnoreRemainingEvents()
            }

        coVerify(exactly = 0) { userDataStore.updateUserData(accessToken = any(), userImageUrl = any()) }
    }

    companion object {
        private val authenticatedUser = AuthenticatedUser(
            description = null, facebookId = null, followersCount = 0, followeesCount = 0, githubLoginName = null, id = "watabee",
            imageMonthlyUploadLimit = 0, imageMonthlyUploadRemaining = 0, itemsCount = 0, linkedinId = null, location = null,
            name = null, organization = null, permanentId = 1, profileImageUrl = "https://xxx.xxx/watabee/image.jpeg",
            teamOnly = false, twitterScreenName = null, websiteUrl = null
        )
    }
}
