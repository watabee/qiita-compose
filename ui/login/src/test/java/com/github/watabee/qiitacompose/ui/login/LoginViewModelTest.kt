package com.github.watabee.qiitacompose.ui.login

import app.cash.turbine.test
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.AccessTokens
import com.github.watabee.qiitacompose.api.response.AuthenticatedUser
import com.github.watabee.qiitacompose.api.response.Error
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var testCoroutineDispatcher: TestCoroutineDispatcher
    @MockK private lateinit var userDataStore: UserDataStore

    @Before
    fun setup() {
        testCoroutineDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testCoroutineDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `when getAccessTokens and getAuthenticatedUser return success then outputEvent emits SuccessLogin`() = runBlocking {
        val accessTokens = AccessTokens(clientId = "client_id", scopes = emptyList(), token = "token")

        val qiitaRepository: QiitaRepository = mockk {
            coEvery { getAccessTokens(any()) } returns QiitaApiResult.Success(response = accessTokens, rate = null, pagination = null)
            coEvery { getAuthenticatedUser(any()) } returns QiitaApiResult.Success(
                response = authenticatedUser,
                rate = null,
                pagination = null
            )
        }

        val viewModel = LoginViewModel(qiitaRepository, userDataStore)
        viewModel.requestEvent(LoginInputEvent.RequestAccessTokens("code"))

        viewModel.outputEvent
            .test {
                Truth.assertThat(awaitItem()).isSameInstanceAs(LoginOutputEvent.SuccessLogin)
                cancelAndIgnoreRemainingEvents()
            }

        coVerify(exactly = 1) { qiitaRepository.getAccessTokens("code") }
        coVerify(exactly = 1) { qiitaRepository.getAuthenticatedUser(accessToken = "token") }
        coVerify(exactly = 1) { userDataStore.updateUserData(accessToken = "token", userImageUrl = authenticatedUser.profileImageUrl) }
    }

    @Test
    fun `when getAccessTokens returns failure then outputEvent emits FailureLogin`() = runBlocking {
        val qiitaRepository: QiitaRepository = mockk {
            coEvery { getAccessTokens(any()) } returns QiitaApiResult.Failure.HttpFailure(
                statusCode = 403,
                error = Error(message = "Forbidden", type = "forbidden"),
                rate = null
            )
        }

        val viewModel = LoginViewModel(qiitaRepository, userDataStore)
        viewModel.requestEvent(LoginInputEvent.RequestAccessTokens("code"))

        viewModel.outputEvent
            .test {
                val expectItem = awaitItem() as? LoginOutputEvent.FailureLogin
                with(expectItem) {
                    Truth.assertThat(this).isNotNull()
                    Truth.assertThat(this?.code).isEqualTo("code")
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
