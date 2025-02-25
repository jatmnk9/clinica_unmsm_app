// GeminiModels.kt
package com.example.proyectocita.api

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val output: Output
)

data class Output(
    val parts: List<Part>
)
