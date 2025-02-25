package com.example.proyectocita

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectocita.databinding.ActivityChatbotBinding

class ChatbotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla la vista con View Binding
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón de retroceso
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Configuración del RecyclerView
        adapter = MessageAdapter(messages)
        binding.recyclerViewMessages.adapter = adapter
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)

        // Botón "Enviar"
        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextMessage.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // Agrega el mensaje del usuario
                messages.add(Message(userInput, isBot = false))

                // Aquí podrías llamar a la API de Gemini para obtener la respuesta.
                // Por ahora, simulamos con un texto fijo:
                messages.add(Message("Respuesta del bot", isBot = true))

                // Notifica cambios
                adapter.notifyDataSetChanged()
                binding.editTextMessage.text.clear()
                binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
            }
        }
    }
}
