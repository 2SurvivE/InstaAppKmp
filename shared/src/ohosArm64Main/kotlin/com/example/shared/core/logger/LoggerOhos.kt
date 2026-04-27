package com.example.shared.core.logger

import com.example.shared.core.bridge.getLogServiceApi

internal actual fun logDebug(tag: String, msg: String) {
    getLogServiceApi().debug(tag, msg)
}

internal actual fun logInfo(tag: String, msg: String) {
    getLogServiceApi().info(tag, msg)
}

internal actual fun logWarn(tag: String, msg: String) {
    getLogServiceApi().warn(tag, msg)
}

internal actual fun logError(tag: String, msg: String) {
    getLogServiceApi().error(tag, msg)
}

internal actual fun logFatal(tag: String, msg: String) {
    getLogServiceApi().fatal(tag, msg)
}