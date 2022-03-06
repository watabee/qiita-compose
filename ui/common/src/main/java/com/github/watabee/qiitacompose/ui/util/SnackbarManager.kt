package com.github.watabee.qiitacompose.ui.util

import androidx.annotation.StringRes
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class Message(val id: Long, @StringRes val messageId: Int, val duration: SnackbarDuration, val action: Action?) {
    data class Action(@StringRes val messageId: Int, val onDismiss: ((SnackbarResult) -> Unit)? = null)
}

@Singleton
class SnackbarManager @Inject constructor() {

    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    fun showMessage(@StringRes messageTextId: Int, duration: SnackbarDuration = SnackbarDuration.Short, action: Message.Action? = null) {
        _messages.update { currentMessages ->
            currentMessages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageTextId,
                duration = duration,
                action = action
            )
        }
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}
