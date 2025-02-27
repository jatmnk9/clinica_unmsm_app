package com.example.proyectocita.api

// Objeto de solicitud para la API de Gemini.
data class GeminiRequest(
    val contents: List<Content>,
    val safetySettings: List<SafetySetting>? = null,
    val generationConfig: GenerationConfig? = null
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

// Configuración de seguridad (opcional).
data class SafetySetting(
    val category: String,
    val threshold: String
)

// Configuración de generación: aquí limitamos la respuesta a 50 tokens.
data class GenerationConfig(
    val stopSequences: List<String>? = null,
    val temperature: Double? = null,
    val maxOutputTokens: Int? = null,
    val topP: Double? = null,
    val topK: Int? = null
)

// Objeto de respuesta de la API.
data class GeminiResponse(
    val candidates: List<Candidate>?
)

// Cada candidato ahora utiliza el campo "content".
data class Candidate(
    val content: ContentData? = null,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null,
    val avgLogprobs: Double? = null
)

data class ContentData(
    val parts: List<Part>? = null,
    val role: String? = null
)

data class SafetyRating(
    val category: String? = null,
    val probability: String? = null
)
