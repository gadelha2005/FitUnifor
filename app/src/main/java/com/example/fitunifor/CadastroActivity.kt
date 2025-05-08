package com.example.fitunifor

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa Firebase Auth e Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.buttonCadastrar.setOnClickListener {
            val nomeCompleto = binding.textNomeCompleto.text.toString().trim()
            val email = binding.textEmail.text.toString().trim()
            val senha = binding.textSenha.text.toString().trim()
            val confirmarSenha = binding.textConfirmarSenha.text.toString().trim()

            if (validarCampos(nomeCompleto, email, senha, confirmarSenha)) {
                cadastrarUsuario(nomeCompleto, email, senha)
            }
        }
    }

    private fun validarCampos(
        nomeCompleto: String,
        email: String,
        senha: String,
        confirmarSenha: String
    ): Boolean {
        if (nomeCompleto.isEmpty()) {
            binding.textNomeCompleto.error = "Digite seu nome completo"
            binding.textNomeCompleto.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            binding.textEmail.error = "Digite seu email"
            binding.textEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textEmail.error = "Email inválido"
            binding.textEmail.requestFocus()
            return false
        }

        if (senha.isEmpty()) {
            binding.textSenha.error = "Digite sua senha"
            binding.textSenha.requestFocus()
            return false
        }

        if (senha.length < 6) {
            binding.textSenha.error = "Senha deve ter pelo menos 6 caracteres"
            binding.textSenha.requestFocus()
            return false
        }

        if (confirmarSenha.isEmpty()) {
            binding.textConfirmarSenha.error = "Confirme sua senha"
            binding.textConfirmarSenha.requestFocus()
            return false
        }

        if (senha != confirmarSenha) {
            binding.textSenha.error = "As senhas não coincidem"
            binding.textConfirmarSenha.error = "As senhas não coincidem"
            binding.textSenha.requestFocus()
            return false
        }

        return true
    }

    private fun cadastrarUsuario(nomeCompleto: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Cadastro no Authentication bem-sucedido, agora salva os dados no Firestore
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    salvarDadosAluno(userId, nomeCompleto, email)
                } else {
                    Toast.makeText(
                        this,
                        "Erro no cadastro: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // CadastroActivity.kt
    private fun salvarDadosAluno(userId: String, nomeCompleto: String, email: String) {
        // Determinar se é admin (você pode usar um critério como email específico)
        val isAdmin = email == "admin@fitunifor.com"

        val aluno = hashMapOf(
            "nome" to nomeCompleto,
            "email" to email,
            "tipo" to if (isAdmin) "admin" else "aluno"
        )

        firestore.collection("usuarios")
            .document(userId)
            .set(aluno)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Cadastro realizado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao salvar dados: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}