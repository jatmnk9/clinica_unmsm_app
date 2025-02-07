package com.example.proyectocita

import CitasAdapter
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


class SaludActivity : AppCompatActivity() {

    private lateinit var citaDao: CitaDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CitasAdapter
    private var citas: MutableList<Cita> = mutableListOf()
    private var citaSeleccionada: Cita? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saludmenuactivity)

        // Inicializar base de datos
        val citaDatabase = CitaDatabase.getInstance(this)
        citaDao = citaDatabase.citaDao()

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCancelarCitas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CitasAdapter(citas) { cita ->
            citaSeleccionada = cita
            Toast.makeText(this, "Cita seleccionada: ${cita.fecha} a las ${cita.hora}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Botón para cancelar la cita seleccionada
        findViewById<Button>(R.id.btnCancelarCita).setOnClickListener {
            if (citaSeleccionada != null) {
                eliminarCita(citaSeleccionada!!)
            } else {
                Toast.makeText(this, "Por favor selecciona una cita", Toast.LENGTH_SHORT).show()
            }
        }

        // Cargar citas
        cargarCitas()
    }

    private fun cargarCitas() {
        lifecycleScope.launch {
            val citasCargadas = withContext(Dispatchers.IO) {
                citaDao.obtenerTodasLasCitas()
            }
            citas.clear()
            citas.addAll(citasCargadas)
            adapter.notifyDataSetChanged()
        }
    }

    private fun eliminarCita(cita: Cita) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                citaDao.eliminarCita(cita)
            }
            // Usar el contexto de la actividad para mostrar el Toast
            Toast.makeText(this@SaludActivity, "Cita eliminada: ${cita.fecha} a las ${cita.hora}", Toast.LENGTH_SHORT).show()
            cargarCitas()
        }
    }

}
