package com.example.bluetoothtracker.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.example.bluetoothtracker.data.datasource.BluetoothDeviceTracker
import com.example.bluetoothtracker.data.datasource.BluetoothDeviceTrackerImpl
import com.example.bluetoothtracker.data.repoImpl.BluetoothRepositoryImpl
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.domain.repository.ScannedDeviceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(
        @ApplicationContext context: Context
    ): BluetoothAdapter? {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
        return bluetoothManager?.adapter
    }

    @Provides
    @Singleton
    fun provideBluetoothManager(
        @ApplicationScope applicationScope: CoroutineScope,
        bluetoothAdapter: BluetoothAdapter?,
        scannedDeviceRepository: ScannedDeviceRepository
    ): BluetoothDeviceTracker {
        return BluetoothDeviceTrackerImpl(applicationScope,bluetoothAdapter)
    }

    @Provides
    @Singleton
    fun provideBluetoothRepository(
        bluetoothManager: BluetoothDeviceTracker
    ): BluetoothRepository = BluetoothRepositoryImpl(bluetoothManager)

}