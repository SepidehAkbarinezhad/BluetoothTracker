package com.example.bluetoothtracker.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.example.bluetoothtracker.data.datasource.BluetoothDeviceTracker
import com.example.bluetoothtracker.data.datasource.BluetoothDeviceTrackerImpl
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
    fun provideBluetoothAdapter(
        @ApplicationContext context: Context
    ): BluetoothAdapter? {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
        return bluetoothManager?.adapter
    }

    @Provides
    fun provideBluetoothManager(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter?
    ): BluetoothDeviceTracker {
        return BluetoothDeviceTrackerImpl(context,bluetoothAdapter)
    }

    @Provides
    fun provideBluetoothRepository(
        bluetoothManager: BluetoothDeviceTracker
    ): BluetoothRepository = BluetoothRepositoryImpl(bluetoothManager)

}