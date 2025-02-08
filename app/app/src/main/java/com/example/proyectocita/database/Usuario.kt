package com.example.proyectocita.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val cedula: String, // Se quita autoGenerate = true
    val nombres: String,
    val apellidos: String,
    val tipoCuent: String, // Tipo de cuenta
    val celular: String,
    val tipoDiscapacidad: String,
    val grupoSanguineo: String?,
    val alergias: String?,
    val enfermedades: String?
)
