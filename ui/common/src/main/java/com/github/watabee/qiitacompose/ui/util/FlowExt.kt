package com.github.watabee.qiitacompose.ui.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <E> Flow<E>.launchWhenStarted(lifecycleOwner: LifecycleOwner) {
    var job: Job? = null

    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_START -> {
                    job = source.lifecycleScope.launch {
                        collect()
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    job?.cancel()
                    job = null
                }
                else -> {
                    // do nothing.
                }
            }
        }
    })
}

fun <E> Flow<E>.launchWhenResumed(lifecycleOwner: LifecycleOwner) {
    var job: Job? = null

    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    job = source.lifecycleScope.launch {
                        collect()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    job?.cancel()
                    job = null
                }
                else -> {
                    // do nothing.
                }
            }
        }
    })
}
