package com.github.watabee.qiitacompose.ui.common

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "tag"
private const val DIALOG_EVENT = "dialog_event"
private const val EXTRA_PARAMS = "extra_params"

private const val TITLE = "title"
private const val MESSAGE = "message"
private const val POSITIVE_BUTTON_TITLE = "positive_button_title"
private const val NEGATIVE_BUTTON_TITLE = "negative_button_title"
private const val CANCELABLE = "cancelable"

private const val REQUEST_KEY = "app_dialog_fragment_request_key"

/**
 * AppDialogFragment is a DialogFragment which is used to show a dialog.
 *
 *
 * Usage:
 *
 * // Show AppDialogFragment.
 * AppDialogFragment.Builder()
 *     .title("title")
 *     .message("message")
 *     .positiveButtonTitle("positive")
 *     .negativeButtonTitle("negative")
 *     .cancelable(true)
 *     .show(parentFragmentManager, "success_login_dialog_tag")
 *
 *
 * fun onCreate(savedInstanceState: Bundle?) {
 *    super.onCreate(savedInstanceState)
 *
 *    // Receive dialog events.
 *    setOnAppDialogFragmentEventListener { tag, event, extraParams ->
 *        when (tag) {
 *            "success_login_dialog_tag" -> {
 *                when (event) {
 *                    DialogEvent.POSITIVE_BUTTON_CLICKED -> { /* do something */ }
 *                    DialogEvent.NEGATIVE_BUTTON_CLICKED -> { /* do something */ }
 *                    DialogEvent.CANCELED -> { /* do something */ }
 *                }
 *            }
 *        }
 *    }
 */
class AppDialogFragment private constructor() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .apply {
                arguments?.getString(TITLE)?.let(this::setTitle)
                arguments?.getString(MESSAGE)?.let(this::setMessage)
                arguments?.getString(POSITIVE_BUTTON_TITLE)?.let {
                    setPositiveButton(it) { _, _ -> setFragmentResult(DialogEvent.POSITIVE_BUTTON_CLICKED) }
                }
                arguments?.getString(NEGATIVE_BUTTON_TITLE)?.let {
                    setNegativeButton(it) { _, _ -> setFragmentResult(DialogEvent.NEGATIVE_BUTTON_CLICKED) }
                }
                setCancelable(requireArguments().getBoolean(CANCELABLE))
                setOnCancelListener { setFragmentResult(DialogEvent.CANCELED) }
            }
            .create()
    }

    private fun setFragmentResult(event: DialogEvent) {
        setFragmentResult(
            REQUEST_KEY,
            Bundle().apply {
                arguments?.getString(TAG)?.let { putString(TAG, it) }
                putSerializable(DIALOG_EVENT, event)
                arguments?.getBundle(EXTRA_PARAMS)?.let { putBundle(EXTRA_PARAMS, it) }
            }
        )
    }

    class Builder {
        private var title: String? = null
        private var message: String? = null
        private var positiveButtonTitle: String? = null
        private var negativeButtonTitle: String? = null
        private var cancelable: Boolean = true
        private var extraParams: Bundle? = null

        fun title(title: String): Builder = apply { this.title = title }

        fun message(message: String): Builder = apply { this.message = message }

        fun positiveButtonTitle(title: String): Builder = apply { this.positiveButtonTitle = title }

        fun negativeButtonTitle(title: String): Builder = apply { this.negativeButtonTitle = title }

        fun cancelable(cancelable: Boolean): Builder = apply { this.cancelable = cancelable }

        fun extraParams(params: Bundle): Builder = apply { this.extraParams = params }

        fun show(fragmentManager: FragmentManager, tag: String? = null) {
            AppDialogFragment().apply {
                arguments = Bundle().apply {
                    title?.let { putString(TITLE, it) }
                    message?.let { putString(MESSAGE, it) }
                    positiveButtonTitle?.let { putString(POSITIVE_BUTTON_TITLE, it) }
                    negativeButtonTitle?.let { putString(NEGATIVE_BUTTON_TITLE, it) }
                    putBoolean(CANCELABLE, cancelable)
                    tag?.let { putString(TAG, it) }
                    extraParams?.let { putBundle(EXTRA_PARAMS, it) }
                }
            }.show(fragmentManager, tag)
        }
    }
}

enum class DialogEvent {
    POSITIVE_BUTTON_CLICKED,
    NEGATIVE_BUTTON_CLICKED,
    CANCELED
}

fun Fragment.setOnAppDialogFragmentEventListener(onEvent: (tag: String?, event: DialogEvent, extraParams: Bundle?) -> Unit) {
    setFragmentResultListener(REQUEST_KEY) { _, bundle ->
        val tag: String? = bundle.getString(TAG)
        val event: DialogEvent = bundle.getSerializable(DIALOG_EVENT) as DialogEvent
        val extraParams: Bundle? = bundle.getBundle(EXTRA_PARAMS)
        onEvent(tag, event, extraParams)
    }
}
