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

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvCreatePassword = findViewById<TextView>(R.id.tvCreatePassword)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)

        // Instancia de la base de datos
        val db = CitaDatabase.getInstance(this)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar si el usuario es "Administrador"
            if (username == "Administrador" && password == "1234") {
                guardarUsuarioEnSesion("admin", true) // Se guarda como "admin"
                startActivity(Intent(this@MainActivity, Administrador::class.java))
                finish()
                return@setOnClickListener
            }

            // Verificar usuario en la base de datos
            lifecycleScope.launch {
                val usuario = withContext(Dispatchers.IO) {
                    db.usuarioDao().getUsuarioByCedula(password)
                }

                if (usuario != null && usuario.nombres.equals(username, ignoreCase = true)) {
                    guardarUsuarioEnSesion(usuario.nombres, false) // Guardamos la cédula
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, MenuActivity::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvCreatePassword.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun guardarUsuarioEnSesion(cedula: String, esAdministrador: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putString("usuarioCedula", cedula) // Guardamos la cédula en lugar de ID
        editor.putBoolean("esAdministrador", esAdministrador)
        editor.apply()
    }
}
