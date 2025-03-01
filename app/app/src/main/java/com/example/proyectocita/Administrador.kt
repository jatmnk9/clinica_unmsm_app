package com.example.proyectocita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class Administrador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrador)


        val btncitapro = findViewById<Button>(R.id.btnCancelarCita)
        val btnVerCita = findViewById<Button>(R.id.btnVerCita)
        val btnCerrarS = findViewById<Button>(R.id.btnCerrarS)


        btncitapro.setOnClickListener {
            val intent = Intent(this, SaludActivity::class.java)
            startActivity(intent)
        }
        btnVerCita.setOnClickListener {
            val intent = Intent(this, CitasActivity::class.java)
            startActivity(intent)
        }
        btnCerrarS.setOnClickListener {
            // Limpiar el estado de la sesión
            val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear() // Elimina todos los datos de sesión
            editor.apply()

            // Redirigir a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Finaliza MenuActivity
        }


    }
}