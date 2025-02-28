package com.example.proyectocita

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectocita.database.CitaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.etUsername) // Correo (aunque se llame etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword) // Contraseña
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvCreatePassword = findViewById<TextView>(R.id.tvCreatePassword)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)

        // Instancia de la base de datos
        val db = CitaDatabase.getInstance(this)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim() // Correo (aunque se llame username)
            val password = etPassword.text.toString().trim() // Contraseña

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar si el usuario es "Administrador"
            if (username == "admin@unmsm.edu.pe" && password == "1234") {
                guardarUsuarioEnSesion("admin@unmsm.edu.pe", true) // Guardar correo de admin
                startActivity(Intent(this@MainActivity, Administrador::class.java))
                finish()
                return@setOnClickListener
            }

            // Verificar usuario en la base de datos
            lifecycleScope.launch {
                val usuario = withContext(Dispatchers.IO) {
                    db.usuarioDao().getUsuarioByCedula(username) // Buscar por correo
                }

                if (usuario != null && usuario.cedula == password) { // Comparar contraseña
                    guardarUsuarioEnSesion(usuario.nombres, false) // Guardar correo del usuario
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, MenuActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvCreatePassword.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun guardarUsuarioEnSesion(correo: String, esAdministrador: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putString("usuarioCedula", correo) // Guardar el correo en "usuarioCedula"
        editor.putBoolean("esAdministrador", esAdministrador)
        editor.apply()
    }
}