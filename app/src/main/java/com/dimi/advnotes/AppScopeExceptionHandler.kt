package com.dimi.advnotes

import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

const val DISCARD_BLANK_NOTE_EXCEPTION_MESSAGE = "The blank note is discarded"

@Singleton
class AppScopeCoroutineExceptionHandler @Inject constructor() :
    AbstractCoroutineContextElement(CoroutineExceptionHandler),
    CoroutineExceptionHandler {

    var appScopeExceptionUIHandler: AppScopeExceptionUIHandler? = null

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        appScopeExceptionUIHandler?.handleException(exception)
    }
}

interface AppScopeExceptionUIHandler {
    fun handleException(exception: Throwable)
}