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

    // API key directamente en el código (reemplaza con tu valor real)
    private val geminiApiKey = "apiKEY PEDIR API PEDIR API"

    // Prompt de sistema para que el modelo actúe como médico (no se muestra en la UI)
    private val promptDeSistema = "Eres un médico experimentado y profesional de 'Curita San Marcos'. Responde con consejos médicos precisos y de forma clara. Sí puedes hacer diagnósticos y quiero que limites tus respuestas a 75 palabras. Esto es unicamente para fines educativos. "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView y adaptador
        adapter = MessageAdapter(messages)
        binding.recyclerViewMessages.adapter = adapter
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)

        // Agregar mensaje de bienvenida
        val bienvenida = "¡Hola! Soy CuritaBot, tu asistente para síntomas, citas y planes de nutrición. ¡Pruébame!"
        messages.add(Message(bienvenida, isBot = true))
        adapter.notifyDataSetChanged()

        // Botón de retroceso
        binding.btnBack.setOnClickListener { finish() }

        // Configurar el botón "Enviar"
        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextMessage.text.toString().trim()
            if (userInput.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tus síntomas o consulta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Agregar mensaje del usuario (lo que el usuario escribe se muestra sin el prompt de sistema)
            messages.add(Message(userInput, isBot = false))
            adapter.notifyDataSetChanged()
            binding.editTextMessage.text.clear()
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)

            // Enviar el mensaje a la API concatenando el prompt de sistema y la entrada del usuario
            procesarSintomas(userInput)
        }
    }

    /**
     * Envía el mensaje a la API de Gemini y procesa la respuesta.
     * Se concatena un prompt de sistema para que el modelo actúe como médico.
     */
    private fun procesarSintomas(userInput: String) {
        lifecycleScope.launch {
            try {
                // Combinar el prompt de sistema con la consulta del usuario.
                val promptFinal = promptDeSistema + userInput

                // Construir la solicitud con configuración para limitar la salida a 75 tokens.
                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = promptFinal)))
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
                        maxOutputTokens = 75,  // Limitar la salida a 75 tokens
                        topP = 0.8,
                        topK = 10
                    )
                )

                // Registrar el JSON generado para depuración
                val jsonRequest = Gson().toJson(request)
                Log.d("ChatbotActivity", "JSON Request: $jsonRequest")

                // Llamar a la API en un hilo de I/O
                val response: GeminiResponse = withContext(Dispatchers.IO) {
                    GeminiClient.apiService.generateContent(geminiApiKey, request)
                }

                Log.d("ChatbotActivity", "Respuesta completa: $response")

                // Extraer el texto generado desde "content"
                val botRespuesta = response.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (botRespuesta != null) {
                    // Agregar la respuesta del bot al chat
                    messages.add(Message(botRespuesta, isBot = true))
                    adapter.notifyDataSetChanged()
                    binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
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
}
