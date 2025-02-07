package com.example.proyectocita

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectocita.database.CitaDao
import com.example.proyectocita.database.CitaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransitoActivity : AppCompatActivity() {
    private lateinit var citaDao: CitaDao
    private lateinit var citadatabase: CitaDatabase
    private lateinit var scrollView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transitomenuactivity)

        // Inicializar base de datos
        citadatabase = CitaDatabase.getInstance(this)
        citaDao = citadatabase.citaDao()

        // Referencia al LinearLayout dentro del ScrollView
        scrollView = findViewById(R.id.scrollViewCitas)

        // Cargar las citas desde la base de datos
        cargarCitas()
    }

    private fun cargarCitas() {
        lifecycleScope.launch {
            // Cargar las citas desde la base de datos en el hilo de fondo
            val citasCargadas = withContext(Dispatchers.IO) {
                citaDao.obtenerTodasLasCitas()  // Asumiendo que tienes esta función en tu Dao
            }

            // Limpiar las vistas previas
            scrollView.removeAllViews()

            // Agregar las citas al LinearLayout
            for (cita in citasCargadas) {
                val textView = TextView(this@TransitoActivity).apply {
                    text = "Cita: ${cita.fecha} - ${cita.hora}"
                    setPadding(16, 16, 16, 16)
                    textSize = 18f
                }
                scrollView.addView(textView)
            }

            // Mostrar un mensaje con la cantidad de citas cargadas
            Toast.makeText(
                this@TransitoActivity,
                "Citas cargadas: ${citasCargadas.size}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
