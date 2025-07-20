package com.example.bluetoothtracker.di

import android.content.Context
import androidx.room.Room
import com.example.bluetoothtracker.data.datasource.BluetoothDeviceTracker
import com.example.bluetoothtracker.data.datasource.ScannedDeviceLocalDataSource
import com.example.bluetoothtracker.data.datasource.ScannedDeviceLocalDataSourceImpl
import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDao
import com.example.bluetoothtracker.data.datasource.room.ScannedDeviceDatabase
import com.example.bluetoothtracker.data.repoImpl.BluetoothRepositoryImpl
import com.example.bluetoothtracker.data.repoImpl.ScannedDeviceRepositoryImpl
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
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

    @Provides
    @Singleton
    fun provideScannedDeviceRepository(
        localDataSource: ScannedDeviceLocalDataSource
    ): ScannedDeviceRepository = ScannedDeviceRepositoryImpl(localDataSource)

    @Provides
    @Singleton
    fun provideScannedDeviceLocalDataSource(
        dao: ScannedDeviceDao
    ): ScannedDeviceLocalDataSource = ScannedDeviceLocalDataSourceImpl(dao)
}