package com.github.watabee.qiitacompose.ui.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AppAlertDialog(
    title: String? = null,
    text: String? = null,
    confirmButtonText: String,
    onConfirmButtonClicked: (() -> Unit)? = null,
    dismissButtonText: String? = null,
    onDismissButtonClicked: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title?.let { { Text(text = it) } },
        text = text?.let { { Text(text = it) } },
        confirmButton = {
            TextButton(onClick = { onConfirmButtonClicked?.invoke() }) { Text(text = confirmButtonText) }
        },
        dismissButton = dismissButtonText?.let {
            { TextButton(onClick = { onDismissButtonClicked?.invoke() }) { Text(text = it) } }
        },
    )
}
