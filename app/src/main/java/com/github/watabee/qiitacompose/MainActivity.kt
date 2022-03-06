package com.github.watabee.qiitacompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.util.SnackbarManager
import com.github.watabee.qiitacompose.util.Env
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var snackbarManager: SnackbarManager
    @Inject lateinit var env: Env

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val appState =
                rememberAppState(qiitaClientId = env.qiitaClientId, userRepository = userRepository, snackbarManager = snackbarManager)
            QiitaApp(appState = appState)
        }
    }
}
