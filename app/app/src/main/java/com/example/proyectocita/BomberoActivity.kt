package com.example.proyectocita

import android.content.Intent
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectocita.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import android.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

class BomberoActivity : AppCompatActivity() {
    private lateinit var citaDao: CitaDao
    private lateinit var doctorDao: DoctorDao
    private lateinit var citadatabase: CitaDatabase
    private lateinit var spinner: Spinner
    private lateinit var calendarView: CalendarView
    private lateinit var especialidad: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bomberomenuactivity)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // Inicializar base de datos
        citadatabase = CitaDatabase.getInstance(this)
        citaDao = citadatabase.citaDao()
        doctorDao = citadatabase.doctorDao()

        // Inicializar vistas
        calendarView = findViewById(R.id.calendarView)
        spinner = findViewById(R.id.spinnerEspecialidad)

        // Obtener la especialidad desde el Intent
        especialidad = intent.getStringExtra("especialidad") ?: ""

        inicializarDoctores()
        cargarDoctoresPorEspecialidad()

        // Configurar CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            mostrarSelectorHora(selectedDate)
        }
    }

    private fun inicializarDoctores() {
        lifecycleScope.launch {
            try {
                val doctoresExistentes = withContext(Dispatchers.IO) {
                    doctorDao.obtenerDoctores()
                }

                if (doctoresExistentes.isEmpty()) {

                    val doctores = listOf(
                        Doctor(nombre = "Dr. Juan Pérez", especialidad = "Medicina General"),
                        Doctor(nombre = "Dra. María López", especialidad = "Medicina General"),
                        Doctor(nombre = "Dr. Luis González", especialidad = "Odontología"),
                        Doctor(nombre = "Dra. Ana Rodríguez", especialidad = "Odontología"),
                        Doctor(nombre = "Dr. Javier Martínez", especialidad = "Psicología"),
                        Doctor(nombre = "Dra. Eliana Castro", especialidad = "Psicología"),
                        Doctor(nombre = "Dra. Rosa García", especialidad = "Ginecología"),
                        Doctor(nombre = "Dr. Manuel Ruiz", especialidad = "Ginecología"),
                        Doctor(nombre = "Dr. Pablo Sánchez", especialidad = "Medicina Familiar"),
                        Doctor(nombre = "Dra. Susana Gómez", especialidad = "Medicina Familiar")
                    )
                    withContext(Dispatchers.IO) {
                        doctorDao.insertarDoctores(doctores)
                    }
                }

            } catch (e: Exception) {
                Log.e("BomberoActivity", "Error al inicializar doctores: ${e.message}")
                Toast.makeText(this@BomberoActivity, "Error al inicializar doctores", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarDoctoresPorEspecialidad() {
        lifecycleScope.launch {
            try {
                val doctores = withContext(Dispatchers.IO) {
                    doctorDao.obtenerDoctoresPorEspecialidad(especialidad)
                }

                val nombresDoctores = if (doctores.isNotEmpty()) {
                    doctores.map { it.nombre }
                } else {
                    listOf("No hay doctores disponibles")
                }

                val adapter = ArrayAdapter(
                    this@BomberoActivity,
                    android.R.layout.simple_spinner_item,
                    nombresDoctores
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.isEnabled = doctores.isNotEmpty()

            } catch (e: Exception) {
                Toast.makeText(this@BomberoActivity, "Error al cargar doctores", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 1. Declarar las variables de clase
    private var selectedMinute: Int = 0
    private var adjustedMinute: Int = 0
    private var selectedHour: Int = 0

    private fun mostrarSelectorHora(fecha: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            this,
            { _, selectedHourValue, selectedMinuteValue ->
                // Ajustar minutos a 00 o 30
                selectedHour = selectedHourValue
                selectedMinute = selectedMinuteValue // Guardamos el minuto seleccionado
                adjustedMinute = if (selectedMinute < 30) 0 else 30  // Ajustamos el minuto

                val horaSeleccionada = String.format("%02d:%02d", selectedHour, adjustedMinute)

                // Verificar si la hora está dentro del rango permitido
                if (selectedHour in 8..18) { // 8 am a 6 pm
                    verificarDisponibilidadCita(fecha, horaSeleccionada)
                } else {
                    Toast.makeText(this, "Selecciona una hora entre 8 am y 6 pm", Toast.LENGTH_SHORT).show()
                }
            },
            hour,
            if (minute < 30) 0 else 30, // Forzar minutos a 00 o 30
            false // Mostrar formato de 24 horas
        )

        // Restringir la selección de minutos a 00 y 30
        timePicker.setOnShowListener {
            val minutePicker = timePicker.findViewById<NumberPicker>(
                this.resources.getIdentifier("minute", "id", "android")
            )
            minutePicker?.apply {
                minValue = 0
                maxValue = 1
                displayedValues = arrayOf("00", "30")
                wrapSelectorWheel = false
            }
        }

        timePicker.setTitle("Selecciona la hora de tu cita")
        timePicker.show()
    }

    private fun verificarDisponibilidadCita(fecha: String, hora: String) {
        lifecycleScope.launch {
            try {
                val citaExistente = withContext(Dispatchers.IO) {
                    citaDao.obtenerCitaPorFechaHoraDoctor(fecha, hora, spinner.selectedItem.toString())
                }

                if (citaExistente == null) {
                    guardarCitaEnBaseDeDatos(fecha, hora)
                } else {
                    Toast.makeText(this@BomberoActivity, "No disponible para la fecha y hora seleccionada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BomberoActivity, "Error al verificar disponibilidad", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerUsuarioCedulaActual(): String? {
        val sharedPreferences = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("usuarioCedula", null)
    }

    private fun guardarCitaEnBaseDeDatos(fecha: String, hora: String) {
        val usuarioCedula = obtenerUsuarioCedulaActual()
        if (usuarioCedula.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No se encontró la cédula del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val cita = Cita(
            fecha = fecha,
            hora = hora,
            id = 0,
            nombreDoctor = spinner.selectedItem.toString(),
            especialidad = especialidad,
            usuarioId = obtenerUsuarioCedulaActual() ?: ""
        )

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    citaDao.insertarCita(cita) // Inserta la cita en la base de datos
                }
                Toast.makeText(this@BomberoActivity, "Cita programada correctamente", Toast.LENGTH_SHORT).show()

                if (selectedMinute != adjustedMinute) {
                    // Mostrar un pop-up informando que se ajustaron los minutos
                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@BomberoActivity)
                            .setTitle("Hora ajustada")
                            .setMessage("Las citas solo pueden ser a la hora exacta o con 30 minutos. Se ajustó a: $selectedHour:${String.format("%02d", adjustedMinute)}.")
                            .setPositiveButton("Aceptar") { dialog, _ ->
                                dialog.dismiss() // Cierra el diálogo
                                // Redirige a CitasActivity después de que el usuario presiona "Aceptar"
                                val intent = Intent(this@BomberoActivity, CitasActivity::class.java)
                                startActivity(intent) // Inicia la nueva actividad
                                finish() // Cierra BomberoActivity si no quieres que el usuario regrese a ella
                            }
                            .show()
                    }
                } else {
                    // Redirige a CitasActivity inmediatamente después de que la cita se haya guardado
                    val intent = Intent(this@BomberoActivity, CitasActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BomberoActivity, "Error al guardar la cita", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
