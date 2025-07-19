package com.example.bluetoothtracker.di

import android.content.Context
import androidx.room.Room
import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDao
import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScannedDeviceDatabase {
        return Room.databaseBuilder(
            context,
            ScannedDeviceDatabase::class.java,
            "bluetooth_scanner_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBluetoothDeviceDao(db: ScannedDeviceDatabase): ScannedDeviceDao {
        return db.deviceDao()
    }
}