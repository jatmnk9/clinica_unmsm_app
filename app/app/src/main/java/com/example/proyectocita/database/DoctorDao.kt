package com.example.proyectocita.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DoctorDao {
    @Insert
    suspend fun insertarDoctores(doctores: List<Doctor>) // Cambié a List<Doctor>

// Cambiar el parámetro para aceptar una lista

    @Query("SELECT * FROM doctores")
    suspend fun obtenerDoctores(): List<Doctor>

    @Query("DELETE FROM doctores")
    suspend fun borrarTodosLosDoctores()

    @Query("SELECT * FROM doctores WHERE especialidad = :especialidad")
    suspend fun obtenerDoctoresPorEspecialidad(especialidad: String): List<Doctor>
}
