package com.example.shared.core.logger

private const val DEFAULT_TAG: String = "Kotlin"
private const val DEFAULT_MSG: String = "Empty msg"

fun Any.DEBUG(msg: String = DEFAULT_MSG) {
    logDebug(ownerTag(this), msg)
}

fun Any.INFO(msg: String = DEFAULT_MSG) {
    logInfo(ownerTag(this), msg)
}

fun Any.WARN(msg: String = DEFAULT_MSG) {
    logWarn(ownerTag(this), msg)
}

fun Any.ERROR(msg: String = DEFAULT_MSG) {
    logError(ownerTag(this), msg)
}

fun Any.FATAL(msg: String = DEFAULT_MSG) {
    logFatal(ownerTag(this), msg)
}

private fun ownerTag(owner: Any): String {
    return owner::class.simpleName ?: DEFAULT_TAG
}
internal expect fun logDebug(tag: String, msg: String)

internal expect fun logInfo(tag: String, msg: String)

internal expect fun logWarn(tag: String, msg: String)

internal expect fun logError(tag: String, msg: String)

internal expect fun logFatal(tag: String, msg: String)