package com.librats

actual class Rats {

    private val jni by lazy {
        RatsClient()
    }

    actual val abiVersion: Int by lazy {
        jni.abiVersion
    }

}