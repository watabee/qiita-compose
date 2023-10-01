package com.github.watabee.qiitacompose.ui.state

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import java.util.UUID

@JvmInline
value class ToastMessageId(val id: Long)

@Stable
data class ToastMessage(
    val message: Message,
    /**
     * true: Toast.LENGTH_SHORT false: Toast.LENGTH_LONG
     */
    val isLengthShort: Boolean,
) {
    constructor(message: String, isLengthShort: Boolean = true) : this(Message.StringMessage(message), isLengthShort)
    constructor(@StringRes messageResId: Int, isLengthShort: Boolean = true) : this(Message.ResourceMessage(messageResId), isLengthShort)

    val id: ToastMessageId = ToastMessageId(UUID.randomUUID().mostSignificantBits)

    @Stable
    sealed interface Message {
        class StringMessage(val value: String) : Message
        class ResourceMessage(@StringRes val resId: Int) : Message
    }
}

fun ToastMessage.show(context: Context) {
    val length = if (isLengthShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    when (message) {
        is ToastMessage.Message.StringMessage -> {
            Toast.makeText(context, message.value, length).show()
        }
        is ToastMessage.Message.ResourceMessage -> {
            Toast.makeText(context, message.resId, length).show()
        }
    }
}
