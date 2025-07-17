package com.example.bluetoothtracker.presentation.screen

import timber.log.Timber

fun printLog(message: String, tag :String="BluetoothTrackerDebug") {
    Timber.tag(tag).d(message)
}