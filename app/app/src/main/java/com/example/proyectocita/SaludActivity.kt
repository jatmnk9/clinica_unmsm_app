package com.example.proyectocita

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocita.database.Cita
import com.example.proyectocita.database.CitaDao
import com.example.proyectocita.database.CitaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import CitaAdapter
import android.widget.ImageButton

class SaludActivity : AppCompatActivity() {

    private lateinit var citaDao: CitaDao
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var citasAdapter: CitaAdapter
    private var citaSeleccionada: Cita? = null // Variable para guardar la cita seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saludmenuactivity)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        sharedPreferences = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
        val citaDatabase = CitaDatabase.getInstance(this)
        citaDao = citaDatabase.citaDao()

        recyclerView = findViewById(R.id.recyclerViewCitas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarCitas()

        val btnCancelarCita = findViewById<Button>(R.id.btnCancelarCita)
        btnCancelarCita.setOnClickListener {
            eliminarCitaSeleccionada()
        }
    }

    private fun cargarCitas() {
        lifecycleScope.launch {
            val citasCargadas = withContext(Dispatchers.IO) {
                if (verificarSiEsAdministrador()) {
                    citaDao.obtenerTodasLasCitas() // El administrador ve todas las citas
                } else {
                    val usuarioCedula = obtenerUsuarioCedulaActual()
                    if (usuarioCedula != null) {
                        citaDao.obtenerCitasPorUsuario(usuarioCedula) // El usuario normal solo ve sus citas
                    } else {
                        emptyList() // En caso de error, mostrar lista vacía
                    }
                }
            }

            // Configurar RecyclerView con las citas cargadas
            citasAdapter = CitaAdapter(citasCargadas) { cita ->
                citaSeleccionada = cita
                Toast.makeText(
                    this@SaludActivity,
                    "Cita seleccionada: ${cita.fecha} a las ${cita.hora}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            recyclerView.adapter = citasAdapter
        }
    }

    private fun verificarSiEsAdministrador(): Boolean {
        return sharedPreferences.getBoolean("esAdministrador", false)
    }

    private fun eliminarCitaSeleccionada() {
        val citaAEliminar = citaSeleccionada
        if (citaAEliminar != null) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    citaDao.eliminarCita(citaAEliminar)
                }
                cargarCitas() // Recargar citas después de eliminar
                citaSeleccionada = null // Resetear la cita seleccionada
                Toast.makeText(this@SaludActivity, "Cita eliminada", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@SaludActivity, "Debes seleccionar una cita primero.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerUsuarioCedulaActual(): String? {
        return sharedPreferences.getString("usuarioCedula", null)
    }
}