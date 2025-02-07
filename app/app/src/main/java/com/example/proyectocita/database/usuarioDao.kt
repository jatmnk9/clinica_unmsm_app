package com.example.proyectocita.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    // Inserta un usuario en la base de datos
    @Insert
    suspend fun insert(usuario: Usuario)

    // Obtiene todos los usuarios por tipo (Paciente o Administrador)
    @Query("SELECT * FROM usuarios WHERE tipoCuent = :tipo")
    suspend fun getUsuariosByType(tipo: String): List<Usuario> // Aquí especificamos el tipo List<Usuario>

    // Obtiene todos los usuarios
    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<Usuario> // También especificamos List<Usuario>

    // Obtiene un usuario por su cédula
    @Query("SELECT * FROM usuarios WHERE cedula = :cedula LIMIT 1")
    suspend fun getUsuarioByCedula(cedula: String): Usuario?
}
