package com.example.bluetoothtracker.data.datasource.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bluetoothtracker.data.model.ScannedDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BScannedDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: ScannedDeviceEntity)

    @Query("SELECT * FROM bluetooth_devices")
    fun getAll(): Flow<List<ScannedDeviceEntity>>

    @Query("DELETE FROM bluetooth_devices")
    suspend fun deleteAll()
}