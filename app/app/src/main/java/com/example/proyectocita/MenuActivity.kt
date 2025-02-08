package com.example.proyectocita


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btncita = findViewById<Button>(R.id.btncita)
        val btncitapro = findViewById<Button>(R.id.btncitaprogramadas)
        val btnCancelarcita = findViewById<Button>(R.id.btnCancelarCita)


        btncita.setOnClickListener {
            val intent = Intent(this, ProgramarCitaActivity::class.java)
            startActivity(intent)
        }
        btncitapro.setOnClickListener {
            val intent = Intent(this, CitasActivity::class.java)
            startActivity(intent)
        }
        btnCancelarcita.setOnClickListener {
            val intent = Intent(this, SaludActivity::class.java)
            startActivity(intent)
        }


    }
}