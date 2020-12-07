package com.dimi.advnotes.presentation.common.extensions

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

inline fun <T> LifecycleOwner.observe(liveData: LiveData<T>, crossinline observer: (T) -> Unit) {
    liveData.observe(
        this,
        Observer {
            it?.let { t -> observer(t) }
        }
    )
}

inline fun <T> LifecycleCoroutineScope.collectWhenStarted(
    flow: Flow<T>,
    crossinline observer: (T) -> Unit
) = launchWhenStarted {
    flow.collect {
        observer(it)
    }
}

/**
 * Data Binding do not support LiveData/MutableLiveData properties change
 * so with setting same value again we fix that issue
 */
inline fun <T> MutableLiveData<T>.mutation(actions: (T?) -> Unit) {
    actions(this.value)
    this.value = this.value
}


