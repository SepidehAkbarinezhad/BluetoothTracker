package com.example.bluetoothtracker.data.datasource

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class BluetoothManagerImpl(private val context: Context) : BluetoothManager {


    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
        bluetoothManager?.adapter
    }

    private val _devicesFlow = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true


    override fun startDiscovery() {

    }

    override fun stopDiscovery() {

    }

    override fun observeFoundDevices(): Flow<List<BluetoothDevice>> = _devicesFlow.asStateFlow()
}
