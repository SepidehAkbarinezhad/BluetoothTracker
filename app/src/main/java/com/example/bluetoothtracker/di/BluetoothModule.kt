package com.example.bluetoothtracker.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.example.bluetoothtracker.data.datasource.BluetoothTrackerManager
import com.example.bluetoothtracker.data.datasource.BluetoothTrackerManagerImpl
import com.example.bluetoothtracker.data.repoImpl.BluetoothRepositoryImpl
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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
    ): BluetoothTrackerManager {
        return BluetoothTrackerManagerImpl(context,bluetoothAdapter)
    }

    @Provides
    fun provideBluetoothRepository(
        bluetoothManager: BluetoothTrackerManager
    ): BluetoothRepository = BluetoothRepositoryImpl(bluetoothManager)

}