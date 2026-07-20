package com.librats.jni

import android.util.Log

class RatsJni {
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
}