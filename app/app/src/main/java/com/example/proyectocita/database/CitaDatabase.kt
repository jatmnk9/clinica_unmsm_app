package com.example.proyectocita.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Cita::class, Doctor::class, Usuario::class], version = 2, exportSchema = false)
abstract class CitaDatabase : RoomDatabase() {
    abstract fun citaDao(): CitaDao
    abstract fun doctorDao(): DoctorDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: CitaDatabase? = null

        fun getInstance(context: Context): CitaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CitaDatabase::class.java,
                    "citas_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
