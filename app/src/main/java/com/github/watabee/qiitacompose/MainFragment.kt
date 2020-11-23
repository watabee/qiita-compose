package com.github.watabee.qiitacompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.github.watabee.qiitacompose.ui.QiitaTheme
import com.github.watabee.qiitacompose.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(context = requireContext()).apply {
            setContent {
                QiitaTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {

                        Button(
                            onClick = {
                                parentFragmentManager.commit {
                                    replace<LoginFragment>(R.id.fragment_container_view)
                                    addToBackStack(null)
                                }
                            }
                        ) {
                            Text(text = "ログイン")
                        }
                        //                   Column(Modifier.fillMaxWidth()) {
                        //                        Greeting("Android")
                        //                        Spacer(modifier = Modifier.preferredHeight(8.dp))
                        //                        CoilImage(
                        //                            data =
                        // "https://www.pakutaso.com/shared/img/thumb/tomcat1567_TP_V.jpg")
                        //                    }
                    }
                }
            }
        }
    }
}
