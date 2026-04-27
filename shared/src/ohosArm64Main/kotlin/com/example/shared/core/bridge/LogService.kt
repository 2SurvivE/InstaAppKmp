package com.example.shared.core.bridge

import com.tencent.tmm.knoi.annotation.ServiceConsumer

@ServiceConsumer
interface LogService {
    fun debug(tag: String, msg: String)

    fun info(tag: String, msg: String)

    fun warn(tag: String, msg: String)

    fun error(tag: String, msg: String)

    fun fatal(tag: String, msg: String)
}