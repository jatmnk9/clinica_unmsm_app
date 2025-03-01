package com.example.proyectocita

import android.content.Intent
import CitaAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
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
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.citas_activity)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            // Verificar si el usuario es administrador
            if (verificarSiEsAdministrador()) {
                // Redirigir al menú del administrador
                val intent = Intent(this, Administrador::class.java) // Cambia "AdminMenuActivity" por el nombre de tu actividad del administrador
                startActivity(intent)
            } else {
                // Redirigir al menú del paciente
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }
            finish() // Cierra la actividad actual (opcional)
        }

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)



        // Inicializar la base de datos y el DAO
        val citaDatabase = CitaDatabase.getInstance(this)
        citaDao = citaDatabase.citaDao()

        // Cargar citas según el tipo de usuario
        cargarCitas()
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
            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCitas)
            recyclerView.layoutManager = LinearLayoutManager(this@CitasActivity)

            recyclerView.adapter = CitaAdapter(citasCargadas) { cita ->
                Toast.makeText(
                    this@CitasActivity,
                    "Cita seleccionada: ${cita.fecha} a las ${cita.hora}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun obtenerUsuarioCedulaActual(): String? {
        return sharedPreferences.getString("usuarioCedula", null)
    }

    private fun verificarSiEsAdministrador(): Boolean {
        return sharedPreferences.getBoolean("esAdministrador", false)
    }
}


