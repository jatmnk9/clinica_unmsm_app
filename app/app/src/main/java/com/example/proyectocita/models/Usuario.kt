package com.example.proyectocita.models

data class Usuario(
    val id_usuario: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val tipo_usuario: String = "",
    val codigo: String? = null,
    val fecha_registro: String = ""
)
