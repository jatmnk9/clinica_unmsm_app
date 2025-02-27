package com.example.proyectocita

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectocita.api.GeminiClient
import com.example.proyectocita.api.GeminiRequest
import com.example.proyectocita.api.Content
import com.example.proyectocita.api.Part
import com.example.proyectocita.api.SafetySetting
import com.example.proyectocita.api.GenerationConfig
import com.example.proyectocita.api.GeminiResponse
import com.example.proyectocita.databinding.ActivityChatbotBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatbotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    // API Key expuesta directamente (según tus indicaciones)
    private val geminiApiKey = "aaaaaaPedirAPIKEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView y adaptador
        adapter = MessageAdapter(messages)
        binding.recyclerViewMessages.adapter = adapter
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)

        // Configurar botón de retroceso
        binding.btnBack.setOnClickListener { finish() }

        // Configurar botón "Enviar"
        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextMessage.text.toString().trim()
            if (userInput.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tus síntomas o motivo de consulta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Agregar mensaje del usuario al chat y actualizar RecyclerView
            messages.add(Message(userInput, isBot = false))
            adapter.notifyDataSetChanged()
            binding.editTextMessage.text.clear()
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)

            // Enviar el mensaje a la API de Gemini
            procesarSintomas(userInput)
        }
    }

    /**
     * Envía el mensaje (síntomas) a la API de Gemini y procesa la respuesta.
     */
    private fun procesarSintomas(sintomas: String) {
        lifecycleScope.launch {
            try {
                // Construir el objeto GeminiRequest con configuraciones de seguridad y generación
                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = sintomas)))
                    ),
                    safetySettings = listOf(
                        SafetySetting(
                            category = "HARM_CATEGORY_DANGEROUS_CONTENT",
                            threshold = "BLOCK_ONLY_HIGH"
                        )
                    ),
                    generationConfig = GenerationConfig(
                        stopSequences = listOf("Title"),
                        temperature = 1.0,
                        maxOutputTokens = 800,
                        topP = 0.8,
                        topK = 10
                    )
                )

                // Registrar el JSON generado para depuración
                val jsonRequest = Gson().toJson(request)
                Log.d("ChatbotActivity", "JSON Request: $jsonRequest")

                // Realizar la llamada a la API en un hilo de I/O
                val response: GeminiResponse = withContext(Dispatchers.IO) {
                    GeminiClient.apiService.generateContent(geminiApiKey, request)
                }

                Log.d("ChatbotActivity", "Respuesta completa: $response")

                // Extraer el texto generado utilizando la nueva estructura "content"
                val botRespuesta = response.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (botRespuesta != null) {
                    // Agregar el mensaje del bot al chat
                    messages.add(Message(botRespuesta, isBot = true))
                    adapter.notifyDataSetChanged()
                    binding.recyclerViewMessages.scrollToPosition(messages.size - 1)

                    // Detectar la especialidad (lógica simple)
                    val especialidad = detectarEspecialidad(botRespuesta)
                    if (especialidad != null) {
                        Toast.makeText(this@ChatbotActivity, "Especialidad recomendada: $especialidad", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@ChatbotActivity, "No se pudo detectar una especialidad en la respuesta.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@ChatbotActivity, "No se recibió respuesta del bot.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                messages.add(Message("Error: ${e.message}", isBot = true))
                adapter.notifyDataSetChanged()
                Log.e("ChatbotActivity", "Error al procesar síntomas", e)
            }
        }
    }

    /**
     * Función para detectar una especialidad médica a partir del texto de respuesta del bot.
     * Se basa en la búsqueda de palabras clave simples.
     */
    private fun detectarEspecialidad(texto: String): String? {
        return when {
            texto.contains("cardiología", ignoreCase = true) -> "Cardiología"
            texto.contains("dermatología", ignoreCase = true) -> "Dermatología"
            texto.contains("neurología", ignoreCase = true) -> "Neurología"
            else -> null
        }
    }
}
