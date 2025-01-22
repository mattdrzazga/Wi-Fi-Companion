package com.mattdrzazga.wificompanion

import android.util.Log

fun Any.log(message: String) {
    Log.v(this.javaClass.simpleName, message)
}