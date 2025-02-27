package com.example.proyectocita.api

// Solicitud a la API: incluye el contenido y, opcionalmente, configuraciones de seguridad y generación.
data class GeminiRequest(
    val contents: List<Content>,
    val safetySettings: List<SafetySetting>? = null,
    val generationConfig: GenerationConfig? = null
)

// Cada Content contiene una lista de Part.
data class Content(
    val parts: List<Part>
)

// Cada Part representa un fragmento de texto.
data class Part(
    val text: String
)

// Configuración de seguridad (opcional).
data class SafetySetting(
    val category: String,
    val threshold: String
)

// Configuración de generación (opcional) para ajustar parámetros de salida.
data class GenerationConfig(
    val stopSequences: List<String>? = null,
    val temperature: Double? = null,
    val maxOutputTokens: Int? = null,
    val topP: Double? = null,
    val topK: Int? = null
)

// Respuesta de la API de Gemini.
data class GeminiResponse(
    val candidates: List<Candidate>?
)

// Cada Candidate ahora usa el campo "content" en lugar de "output".
data class Candidate(
    val content: ContentData? = null,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null,
    val avgLogprobs: Double? = null
)

// ContentData contiene los detalles generados, incluyendo una lista de partes y el rol.
data class ContentData(
    val parts: List<Part>? = null,
    val role: String? = null
)

// Información sobre la seguridad de la salida.
data class SafetyRating(
    val category: String? = null,
    val probability: String? = null
)
