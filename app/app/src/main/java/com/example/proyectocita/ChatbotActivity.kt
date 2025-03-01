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
    private val geminiApiKey = "AIzaSyAqUJc8Q01QSpAa4sH33aLAHYTWahp7FaU"

    // Prompt de sistema para que el modelo actúe como médico (no se muestra en la UI)
    private val promptDeSistema =
        "Eres un médico virtual de 'Curita San Marcos'. Basándote en los síntomas proporcionados por el usuario, debes recomendar una única especialidad médica de la lista: " +
        "Cardiología, Dermatología, Gastroenterología, Ginecología, Medicina General, Medicina Interna, Neumología, " +
        "Neurología, Obstetricia, Odontología, Oftalmología, Otorrinolaringología, Traumatología, Pediatría, Psicología, " +
        "Podología, Terapia Física y Rehabilitación y Urología. " +
        "Si te pregunta por donde esta la clínica o el hospital, tu responde 'Av. Jorge Basadre Grohmann, Lima 15081, dentro de la Universidad Nacional Mayor de San Marcos'" +
        "Si te pregunta por el número de especialidades o cuál es el nombre de las especialidades, tú di el número y mencionacelas." +
        "Si te pregunta para que sirve cierta especialidad, tu dale la definición en una oración" +
        "NO respondas con explicaciones largas. Solo responde con el nombre de la especialidad que mejor se ajuste a los síntomas del usuario. " +
        "NO te despidas."



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView y adaptador
        adapter = MessageAdapter(messages)
        binding.recyclerViewMessages.adapter = adapter
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)

        // Agregar mensaje de bienvenida
        val bienvenida = "¡Hola! Soy CuritaBot \uD83E\uDD16 , estoy aquí para diagnosticarte \uD83D\uDCD2 y enviarte a una especialidad según tus síntomas \uD83D\uDC68\u200D⚕\uFE0F\uD83D\uDC69\u200D⚕\uFE0F. " +
                "¿Qué síntomas tienes hoy?"
        messages.add(Message(bienvenida, isBot = true))
        adapter.notifyDataSetChanged()

        // Botón de retroceso
        binding.btnBack.setOnClickListener { finish() }

        // Configurar el botón "Enviar"
        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextMessage.text.toString().trim()
            if (userInput.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tus síntomas o consulta ", Toast.LENGTH_SHORT).show()
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
                // Formatear la solicitud con una separación clara entre contexto y entrada del usuario
                val promptFinal = """
            Contexto:
            $promptDeSistema
            
            Pregunta del usuario:
            $userInput
            
            Respuesta esperada:
            Si el usuario menciona síntomas, responde con un "Recomiendo ir a" (o una oración similar) con el nombre de la especialidad médica 
            Si el usuario pregunta sobre planes, medicamentos, consejos, quías o similar para curarse de sus síntomas, dale un plan de tratamiento básico.
            Si el usuario te saluda, responde con un saludo (que no se solo hola) y pregúntandole sobre los síntomas que tiene. Usa varias palabras
            Si el usuario se despide o agradece, responde "¡Muchas gracias por usar CuritaBot!. Espero haberte ayudado. Cuídate. 😊", no le vuelvas a preguntasr por sus síntomas.
            Si el usuario te pregunta por la razón a la que debe ir a cierta especialidad, responde con una oración que defina la especialidad.
            Para cualquier otra pregunta, dile que vuelva a preguntar, porque no entendi la pregunta.
            """.trimIndent()

                // Construcción de la solicitud
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
                        stopSequences = listOf("\n"),
                        temperature = 0.5,
                        maxOutputTokens = 100,
                        topP = 0.9,
                        topK = 5
                    )
                )

                // Llamar a la API en un hilo de I/O
                val response: GeminiResponse = withContext(Dispatchers.IO) {
                    GeminiClient.apiService.generateContent(geminiApiKey, request)
                }

                // Extraer la respuesta del bot
                val botRespuesta = response.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (botRespuesta != null) {
                    messages.add(Message(botRespuesta, isBot = true))
                    adapter.notifyDataSetChanged()
                    binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
                } else {
                    Toast.makeText(this@ChatbotActivity, "No se recibió respuesta del bot.", Toast.LENGTH_LONG).show()
                }

                // Verificar si el usuario está despidiéndose o agradeciendo
                /*
                val despedidas = listOf("Gracias", "Hasta luego", "Nos vemos", "Adiós","Adios", "Chau", "Hasta pronto", "Hasta la próxima", "Hasta la proxima", "Bye", "Bye bye")
                if (despedidas.any { it.equals(userInput, ignoreCase = true) }) {
                    val respuestaDespedida = "¡Gracias por usar CuritaBot! \uD83E\uDD16 Espero haberte ayudado. Cuídate. 😊"
                    messages.add(Message(respuestaDespedida, isBot = true))
                    adapter.notifyDataSetChanged()
                    binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
                    return@launch
                }
                */

            } catch (e: Exception) {
                messages.add(Message("Error: ${e.message}", isBot = true))
                adapter.notifyDataSetChanged()
                Log.e("ChatbotActivity", "Error al procesar síntomas", e)
            }
        }
    }
}
