package com.example.fitunifor.aluno

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitunifor.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IAActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var scrollContainer: LinearLayout
    private lateinit var voltar: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_ia)

        inputEditText = findViewById(R.id.editTextText)
        sendButton = findViewById(R.id.imageView5)
        scrollContainer = findViewById(R.id.scroll_container)
        voltar = findViewById(R.id.icon_voltar_principal)

        sendButton.setOnClickListener {
            val inputText = inputEditText.text.toString()
            if (inputText.isNotBlank()) {
                addUserMessage(inputText)
                processIA(inputText)
                inputEditText.text.clear()
            }
        }

        voltar.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun addUserMessage(message: String) {
        val userText = TextView(this).apply {
            text = "Você: $message"
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black)) // Cor preta
            setPadding(16, 8, 16, 8)
        }
        scrollContainer.addView(userText)
    }

    private fun addResponseMessage(message: String) {
        val responseText = TextView(this).apply {
            text = "IA: $message"
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black)) // Cor preta
            setPadding(16, 8, 16, 8)
        }
        scrollContainer.addView(responseText)
    }

    private fun processIA(userInput: String) {
        lifecycleScope.launch {
            try {
                val prompt = "Responda como um especialista em academia, musculação e nutrição esportiva. Seja objetivo e claro. Dê uma resposta com no máximo 300 caracteres. Minha pergunta é: $userInput"

                val response = withContext(Dispatchers.IO) {
                    GeminiAI.generativeModel.generateContent(prompt).text.orEmpty()
                }

                val limitedResponse = response.take(300)
                addResponseMessage(limitedResponse)

            } catch (e: Exception) {
                addResponseMessage("Erro ao acessar a IA: ${e.message}")
            }
        }
    }
}
