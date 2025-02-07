package com.example.proyectocita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PoliceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.policiamenuactivity)

        val btnmedigene = findViewById<Button>(R.id.btnMediGene)
        val btnodontologo = findViewById<Button>(R.id.btnOdonto)
        val btnpsicologia = findViewById<Button>(R.id.btnPsicologia)
        val btnGineco = findViewById<Button>(R.id.btnGinec)
        val btnMedicinafa = findViewById<Button>(R.id.btnMedicFam)

        btnmedigene.setOnClickListener { abrirCalendario("Medicina General") }
        btnodontologo.setOnClickListener { abrirCalendario("Odontología") }
        btnpsicologia.setOnClickListener { abrirCalendario("Psicología") }
        btnGineco.setOnClickListener { abrirCalendario("Ginecología") }
        btnMedicinafa.setOnClickListener { abrirCalendario("Medicina Familiar") }
    }

    private fun abrirCalendario(tipo: String) {
        val intent = Intent(this, BomberoActivity::class.java)
        intent.putExtra("especialidad", tipo)
        startActivity(intent)
    }
}
