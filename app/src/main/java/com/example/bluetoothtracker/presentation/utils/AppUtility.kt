package com.example.bluetoothtracker.presentation.utils

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun printLog(message: String, tag :String="BluetoothTrackerDebug") {
    Timber.tag(tag).d(message)
}

fun Long.toReadableDate() : String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(date) ?:""
}