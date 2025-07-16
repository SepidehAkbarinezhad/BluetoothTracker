package com.example.bluetoothtracker.data.repoImpl

import com.example.bluetoothtracker.data.datasource.BluetoothManager
import com.example.bluetoothtracker.domain.data.Device
import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import kotlinx.coroutines.flow.Flow

class BluetoothRepositoryImpl(private val bluetoothManager: BluetoothManager) :
    BluetoothRepository {

    override fun isBluetoothEnabled(): Boolean = bluetoothManager.isBluetoothEnabled()

    override fun getNearbyDevices(): Flow<List<Device>> {
        TODO("Not yet implemented")
    }

    override fun startScan() {
        TODO("Not yet implemented")
    }

    override fun stopScan() {
        TODO("Not yet implemented")
    }
}