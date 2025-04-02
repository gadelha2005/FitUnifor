package com.example.fitunifor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R

class RedefinirSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redefinir_senha)

        // Configura o clique do botão Confirmar
        findViewById<Button>(R.id.button_confirmar_nova_senha).setOnClickListener {
            // Validação básica das senhas
            val novaSenha = findViewById<EditText>(R.id.text_nova_senha).text.toString()
            val confirmacaoSenha = findViewById<EditText>(R.id.text_confirmar_nova_senha).text.toString()

            if (novaSenha.isNotEmpty() && novaSenha == confirmacaoSenha) {
                voltarParaMainComTabLogin()
            } else {
                // Mostra mensagem de erro se as senhas não coincidirem
                findViewById<EditText>(R.id.text_confirmar_nova_senha).error = "As senhas não coincidem"
            }
        }
    }

    private fun voltarParaMainComTabLogin() {
        // Cria uma intent para MainActivity com flag para limpar a pilha
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // Podemos passar um extra para indicar que deve mostrar a tab de login
            putExtra("SHOW_LOGIN_TAB", true)
        }
        startActivity(intent)
        finish() // Finaliza a activity atual
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}