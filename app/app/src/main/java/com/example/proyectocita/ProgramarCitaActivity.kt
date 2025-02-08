package com.example.proyectocita

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProgramarCitaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.programarcita_activity)

        val spinnerEspecialidades = findViewById<Spinner>(R.id.Especialidades)
        val btnConfirmar = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)


        val optionsEspecialidades = arrayOf("Medicina General", "Psicología", "Odontología","Ginecología", "Medicina Familiar")
        spinnerEspecialidades.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsEspecialidades)

        btnConfirmar.setOnClickListener {
            val especialidadSeleccionada = spinnerEspecialidades.selectedItem.toString()

            abrirCalendario(especialidadSeleccionada)
        }

        btnBack.setOnClickListener {
            finish()  // Cierra la actividad y regresa a la anterior
        }
    }

    private fun abrirCalendario(especialidad: String) {
        val intent = Intent(this, BomberoActivity::class.java)
        intent.putExtra("especialidad", especialidad)
        startActivity(intent)
    }
}
