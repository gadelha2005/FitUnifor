package com.example.fitunifor

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.databinding.ActivityEsqueciSenhaBinding
import com.google.firebase.auth.FirebaseAuth

class EsqueciSenhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEsqueciSenhaBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEsqueciSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonVoltarLogin.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.buttonEnviarParaEmail.setOnClickListener {
            validarEmailESalvar()
        }
    }

    private fun validarEmailESalvar() {
        val email = binding.text2Email.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding.text2Email.error = "Digite seu email"
                binding.text2Email.requestFocus()
                showAlert("Campo obrigatório", "Por favor, preencha o email")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.text2Email.error = "Email inválido"
                binding.text2Email.requestFocus()
                showAlert("Email inválido", "Por favor, digite um email válido")
            }
            else -> {
                enviarEmailRedefinicaoSenha(email)
            }
        }
    }

    private fun enviarEmailRedefinicaoSenha(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSuccessDialog(email)
                } else {
                    // Mensagem genérica por segurança (não revela se email existe)
                    Toast.makeText(
                        this,
                        "Se este email estiver cadastrado, você receberá um link de redefinição",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this)
            .setTitle("Email enviado")
            .setMessage("Enviamos um link para redefinir sua senha para $email. Verifique sua caixa de entrada.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            .setCancelable(false)
            .create()
            .show()
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