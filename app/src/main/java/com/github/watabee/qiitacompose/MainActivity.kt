package com.github.watabee.qiitacompose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.github.watabee.qiitacompose.util.Env
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var env: Env

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            QiitaTheme {
                ProvideWindowInsets {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        Column(modifier = Modifier.statusBarsPadding()) {
                            NavGraph(env = env, userRepository = userRepository)
                        }
                    }
                }
            }
        }
    }
}
