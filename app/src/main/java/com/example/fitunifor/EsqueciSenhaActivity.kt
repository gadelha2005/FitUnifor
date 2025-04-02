package com.example.fitunifor

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R

class EsqueciSenhaActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueci_senha)

        editEmail = findViewById(R.id.text2_email)

        // Botão Voltar ao Login
        findViewById<Button>(R.id.button_voltar_login).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Botão Enviar para Email com validação
        findViewById<Button>(R.id.button_enviar_para_email).setOnClickListener {
            validarEmailESalvar()
        }
    }

    private fun validarEmailESalvar() {
        val email = editEmail.text.toString().trim()

        when {
            email.isEmpty() -> {
                editEmail.error = "Digite seu email"
                editEmail.requestFocus()
                showAlert("Campo obrigatório", "Por favor, preencha o email")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                editEmail.error = "Email inválido"
                editEmail.requestFocus()
                showAlert("Email inválido", "Por favor, digite um email válido")
            }
            else -> {
                // Email válido, pode prosseguir
                enviarParaRedefinicaoSenha(email)
            }
        }
    }

    private fun enviarParaRedefinicaoSenha(email: String) {
        Toast.makeText(this, "Email de redefinição enviado para $email", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, RedefinirSenhaActivity::class.java).apply {
            putExtra("EMAIL", email) // Passa o email para a próxima activity
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}