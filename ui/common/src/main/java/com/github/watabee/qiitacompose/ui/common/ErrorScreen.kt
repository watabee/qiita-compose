package com.github.watabee.qiitacompose.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ErrorScreen(onRetryButtonClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
            .padding(horizontal = 16.dp)
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.error),
                modifier = Modifier.requiredSize(32.dp)
            )
            Spacer(modifier = Modifier.requiredWidth(8.dp))
            Text(text = stringResource(id = R.string.common_connection_error_message), style = MaterialTheme.typography.body1)
        }

        Spacer(modifier = Modifier.requiredHeight(32.dp))

        AppOutlinedButton(
            onClick = onRetryButtonClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.common_retry), style = MaterialTheme.typography.button)
        }
    }
}
