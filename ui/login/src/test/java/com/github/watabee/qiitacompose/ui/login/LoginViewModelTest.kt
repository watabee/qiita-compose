package com.github.watabee.qiitacompose.ui.login

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.AccessTokens
import com.github.watabee.qiitacompose.api.response.SuccessResponse
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.testutil.TestCoroutineRule
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {

    @get:Rule val testCoroutineRule = TestCoroutineRule()
    @MockK private lateinit var userDataStore: UserDataStore

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `when requestAccessTokens return success then outputEvent emits SuccessLogin`() = runBlockingTest {
        val accessTokens = AccessTokens(clientId = "client_id", scopes = emptyList(), token = "token")

        val qiitaRepository: QiitaRepository = mockk {
            coEvery { requestAccessTokens(any()) } returns QiitaApiResult.Success(SuccessResponse(response = accessTokens, rate = null))
        }

        val viewModel = LoginViewModel(qiitaRepository, userDataStore)
        viewModel.requestEvent(LoginInputEvent.RequestAccessTokens("code"))

        viewModel.outputEvent
            .test {
                Truth.assertThat(expectItem()).isSameInstanceAs(LoginOutputEvent.SuccessLogin)
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `when requestAccessTokens return failure then outputEvent emits FailureLogin`() = runBlockingTest {
        val qiitaRepository: QiitaRepository = mockk {
            coEvery { requestAccessTokens(any()) } returns QiitaApiResult.Failure.HttpFailure(code = 400, error = null)
        }

        val viewModel = LoginViewModel(qiitaRepository, userDataStore)
        viewModel.requestEvent(LoginInputEvent.RequestAccessTokens("code"))

        viewModel.outputEvent
            .test {
                val expectItem = expectItem() as? LoginOutputEvent.FailureLogin
                with(expectItem) {
                    Truth.assertThat(this).isNotNull()
                    Truth.assertThat(this?.code).isEqualTo("code")
                }

                cancelAndIgnoreRemainingEvents()
            }
    }
}
