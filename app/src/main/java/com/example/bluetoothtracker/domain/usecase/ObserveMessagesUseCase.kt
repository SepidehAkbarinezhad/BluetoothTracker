package com.example.bluetoothtracker.domain.usecase

import com.example.bluetoothtracker.domain.repository.BluetoothRepository
import com.example.bluetoothtracker.presentation.utils.printLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(private val bluetoothRepository: BluetoothRepository) {
    operator fun invoke(): Flow<String> {
       return bluetoothRepository.messageFlow()
    }
}