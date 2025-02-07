package com.example.proyectocita

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.proyectocita.database.CitaDatabase
import com.example.proyectocita.database.Usuario
import kotlinx.coroutines.launch

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val etCedula = findViewById<EditText>(R.id.etCedula)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val tipoCuent = findViewById<Spinner>(R.id.tipodecuenta)
        val etCellphone = findViewById<EditText>(R.id.etCellphone)
        val spBloodType = findViewById<EditText>(R.id.spBloodType)


        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val cedu = etCedula.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val tipodecuenta = tipoCuent.selectedItem.toString()
            val cellphone = etCellphone.text.toString()
            val disabilityType = "No"
            val bloodType = spBloodType.text.toString()
            val allergies = "No"
            val illnesses = "No"

            if (cedu.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() &&
                tipodecuenta.isNotEmpty() && cellphone.isNotEmpty()) {

                // Crear un nuevo objeto Usuario
                val nuevoUsuario = Usuario(
                    cedula = cedu,
                    nombres = firstName,
                    apellidos = lastName,
                    tipoCuent = tipodecuenta,
                    celular = cellphone,
                    grupoSanguineo = disabilityType,
                    tipoDiscapacidad = bloodType,
                    alergias = allergies,
                    enfermedades = illnesses
                )

                // Guardar en la base de datos usando una coroutine
                val db = CitaDatabase.getInstance(this)
                lifecycleScope.launch {
                    db.usuarioDao().insert(nuevoUsuario)
                    runOnUiThread {
                        Toast.makeText(this@CreateAccountActivity, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}