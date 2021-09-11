package com.github.watabee.qiitacompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.watabee.qiitacompose.repository.UserRepository
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class MainFragment : Fragment() {

    @Inject lateinit var userRepository: UserRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(context = requireContext()).apply {
            setContent {
                QiitaTheme {
                    ProvideWindowInsets {
                        // A surface container using the 'background' color from the theme
                        Surface(color = MaterialTheme.colors.background) {
                            Column(modifier = Modifier.statusBarsPadding()) {
                                NavGraph(userRepository = userRepository) {
                                    findNavController().navigate(R.id.nav_login)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
