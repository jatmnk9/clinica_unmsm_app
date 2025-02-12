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




        btncitapro.setOnClickListener {
            val intent = Intent(this, SaludActivity::class.java)
            startActivity(intent)
        }
        btnVerCita.setOnClickListener {
            val intent = Intent(this, CitasActivity::class.java)
            startActivity(intent)
        }



    }
}