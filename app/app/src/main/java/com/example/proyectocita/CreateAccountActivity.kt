package com.example.proyectocita

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectocita.database.CitaDatabase
import com.example.proyectocita.database.Usuario
import kotlinx.coroutines.launch

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val etCedula = findViewById<EditText>(R.id.etCedula) // contraseña
        val etFirstName = findViewById<EditText>(R.id.etFirstName) // correo
        val etLastName = findViewById<EditText>(R.id.etLastName) // apellido
        val tipoCuent = findViewById<Spinner>(R.id.tipodecuenta) // estado actual
        val etCellphone = findViewById<EditText>(R.id.etCellphone) // código
        val spBloodType = findViewById<EditText>(R.id.spBloodType) // nombre

        val btnSave = findViewById<Button>(R.id.btnSave)

        // Deshabilitar el campo de código por defecto
        etCellphone.isEnabled = false

        // Listener para validar el correo electrónico
        etFirstName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Cuando el campo pierde el foco
                val correo = etFirstName.text.toString()
                if (correo.endsWith("@unmsm.edu.pe")) {
                    // Habilitar el campo de código si el correo es válido
                    etCellphone.isEnabled = true
                    etCellphone.hint = "Ingrese su código"
                } else {
                    // Deshabilitar el campo de código si el correo no es válido
                    etCellphone.isEnabled = false
                    etCellphone.hint = "No aplica"
                    etCellphone.text.clear() // Limpiar el campo
                }
            }
        }

        btnSave.setOnClickListener {
            val cedu = etCedula.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val tipodecuenta = tipoCuent.selectedItem.toString()
            val cellphone = etCellphone.text.toString()
            val disabilityType = "No"
            val bloodType = spBloodType.text.toString()

            if (cedu.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || tipodecuenta.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar el código si el correo es de UNMSM
            if (firstName.endsWith("@unmsm.edu.pe") && cellphone.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear un nuevo objeto Usuario
            val nuevoUsuario = Usuario(
                cedula = cedu,
                nombres = firstName,
                apellidos = lastName,
                tipoCuent = tipodecuenta,
                celular = cellphone,
                grupoSanguineo = disabilityType,
                tipoDiscapacidad = bloodType
            )

            // Guardar en la base de datos usando una coroutine
            val db = CitaDatabase.getInstance(this)
            lifecycleScope.launch {
                try {
                    db.usuarioDao().insert(nuevoUsuario)
                    runOnUiThread {
                        Toast.makeText(
                            this@CreateAccountActivity,
                            "Usuario creado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@CreateAccountActivity,
                            "Error al registrar el usuario: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}