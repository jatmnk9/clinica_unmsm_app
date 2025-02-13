package com.example.proyectocita.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    val cedula: String, // Se quita autoGenerate = true
    @PrimaryKey val nombres: String,
    val apellidos: String,
    val tipoCuent: String, // Tipo de cuenta
    val celular: String,
    val tipoDiscapacidad: String,
    val grupoSanguineo: String?
)
