package com.example.proyectocita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.proyectocita.database.CitaDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvCreatePassword = findViewById<TextView>(R.id.tvCreatePassword)

        // Instancia de la base de datos
        val db = CitaDatabase.getInstance(this)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validar entrada
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar si el usuario es "Administrador" y la clave es "1234"
            if (username == "Administrador" && password == "1234") {
                val intent = Intent(this@MainActivity, Administrador::class.java)
                startActivity(intent)
                finish() // Termina la actividad de login para no permitir volver atrás
                return@setOnClickListener
            }

            // Verificar usuario y contraseña en la base de datos
            lifecycleScope.launch {
                val usuario = db.usuarioDao().getUsuarioByCedula(password) // Buscar por cédula
                if (usuario != null && usuario.nombres.equals(username, ignoreCase = true)) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, MenuActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvCreatePassword.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}
