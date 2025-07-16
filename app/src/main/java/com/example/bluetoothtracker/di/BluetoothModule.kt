package com.example.bluetoothtracker.di

import android.content.Context
import com.example.bluetoothtracker.data.datasource.BluetoothManager
import com.example.bluetoothtracker.data.datasource.BluetoothManagerImpl
import com.example.bluetoothtracker.data.repoImpl.BluetoothRepositoryImpl
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothManager(
        @ApplicationContext context: Context
    ): BluetoothManager {
        return BluetoothManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothRepository(
        bluetoothManager: BluetoothManager
    ): BluetoothRepository = BluetoothRepositoryImpl(bluetoothManager)

}