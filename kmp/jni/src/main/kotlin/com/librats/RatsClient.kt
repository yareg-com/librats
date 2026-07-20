package com.librats

import android.util.Log

class RatsClient {
    private val tag: String
        get() = javaClass.simpleName

    init {
        try {
            System.loadLibrary("rats_jni")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(tag, "Failed to load native library", e)
            throw e
        }
    }

    val abiVersion: Int
        get() = nativeAbi()

    external fun nativeAbi(): Int
}