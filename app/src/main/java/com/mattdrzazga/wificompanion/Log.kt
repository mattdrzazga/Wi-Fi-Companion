package com.mattdrzazga.wificompanion

import android.util.Log

fun Any.log(message: String, tag: String = this.javaClass.simpleName) {
    Log.v(tag, message)
}

fun Any.logw(message: String, tag: String = this.javaClass.simpleName) {
    Log.w(tag, message)
}