package com.example.proyectocita

import CitasAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocita.database.CitaDao
import com.example.proyectocita.database.CitaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CitasActivity : AppCompatActivity() {

    private lateinit var citaDao: CitaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.citas_activity)

        // Inicializar la base de datos y el DAO
        val citaDatabase = CitaDatabase.getInstance(this)
        citaDao = citaDatabase.citaDao()

        // Llamar al método para cargar las citas desde la base de datos
        cargarCitas()
    }

    private fun cargarCitas() {
        lifecycleScope.launch {
            val citasCargadas = withContext(Dispatchers.IO) {
                citaDao.obtenerTodasLasCitas()
            }

            // Configurar RecyclerView con las citas cargadas
            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCitas)
            recyclerView.layoutManager = LinearLayoutManager(this@CitasActivity)

            // Pasar la función onCitaSelected
            recyclerView.adapter = CitasAdapter(citasCargadas) { cita ->
                // Aquí defines qué hacer cuando se selecciona una cita
                // Por ejemplo, podrías mostrar un mensaje o abrir una nueva actividad
                Toast.makeText(this@CitasActivity, "Cita seleccionada: ${cita.fecha} a las ${cita.hora}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



