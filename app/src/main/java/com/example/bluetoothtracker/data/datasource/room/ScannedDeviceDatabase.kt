package com.example.bluetoothtracker.data.datasource.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bluetoothtracker.data.model.ScannedDeviceEntity


@Database(entities = [ScannedDeviceEntity::class], version = 1, exportSchema = false)
abstract class ScannedDeviceDatabase : RoomDatabase() {

    abstract fun movieDao(): BScannedDeviceDao
}