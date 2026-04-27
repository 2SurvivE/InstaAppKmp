package com.example.shared.service

import com.tencent.tmm.knoi.annotation.ServiceProvider

import com.example.shared.core.logger.*

@ServiceProvider
open class CalcService {
    fun add(a: Int, b: Int): Int{
        val res = a+b;
        DEBUG("Kotlin Add $a + $b, res = $res")
        return res;
    }
}