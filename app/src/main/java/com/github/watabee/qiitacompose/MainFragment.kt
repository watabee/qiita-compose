package com.github.watabee.qiitacompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(context = requireContext()).apply {
            setContent {
                QiitaTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        NavGraph {
                            findNavController().navigate(R.id.nav_login)
                        }
                    }
                }
            }
        }
    }
}
