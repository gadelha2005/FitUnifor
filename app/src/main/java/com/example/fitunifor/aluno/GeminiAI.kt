package com.example.fitunifor.aluno

import com.google.ai.client.generativeai.GenerativeModel


object GeminiAI {
    private const val API_KEY = "AIzaSyA7zVoqsicaU1vc9ay74UsLSzLIho8uBOA" // Substitua por sua chave real

    val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = API_KEY
    )
}
