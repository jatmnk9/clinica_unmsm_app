package com.example.proyectocita.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "citas")
@Parcelize
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: String,
    val hora: String,
    val nombreDoctor: String,
    val especialidad: String,
    val usuarioId: String // <- Revisa que este sea el nombre correcto
) : Parcelable

