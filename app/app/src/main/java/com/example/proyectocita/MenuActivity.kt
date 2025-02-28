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
        val btnChatbot = findViewById<Button>(R.id.btnChatbot)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)


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
        btnChatbot.setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }
        btnCerrarSesion.setOnClickListener {
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