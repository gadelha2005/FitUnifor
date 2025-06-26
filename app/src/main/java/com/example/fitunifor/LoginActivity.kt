package com.example.fitunifor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.administrador.PainelAdminstrativoActivity
import com.example.fitunifor.aluno.PrincipalActivity
import com.example.fitunifor.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonEntrar.setOnClickListener {
            val email = binding.textEmail.text.toString().trim()
            val senha = binding.textSenha.text.toString().trim()

            if (validarCampos(email, senha)) {
                fazerLogin(email, senha)
            }
        }

        binding.buttonEsqueciSenha.setOnClickListener {
            startActivity(Intent(this, EsqueciSenhaActivity::class.java))
        }
    }

    private fun validarCampos(email: String, senha: String): Boolean {
        if (email.isEmpty()) {
            binding.textEmail.error = "Digite seu email"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textEmail.error = "Email inválido"
            return false
        }

        if (senha.isEmpty()) {
            binding.textSenha.error = "Digite sua senha"
            return false
        }

        return true
    }

    private fun fazerLogin(email: String, senha: String) {
        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    Log.d("LoginActivity", "UID do usuário: $userId")

                    db.collection("usuarios").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val nome = document.getString("nome") ?: "Admin"
                                val tipo = document.getString("tipo") ?: "aluno"

                                Log.d("LoginActivity", "Dados encontrados: Nome=$nome, Tipo=$tipo")

                                redirecionarUsuario(nome, tipo)
                            } else {
                                Log.w("LoginActivity", "Documento não encontrado")
                                Toast.makeText(this, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Erro ao buscar usuário", e)
                            Toast.makeText(this, "Erro ao buscar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("LoginActivity", "Falha no login", task.exception)
                    Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun redirecionarUsuario(nomeUsuario: String, tipoUsuario: String) {
        val intent = if (tipoUsuario == "admin") {
            Intent(this, PainelAdminstrativoActivity::class.java).apply {
                putExtra("NOME_USUARIO", nomeUsuario)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            Intent(this, PrincipalActivity::class.java).apply {
                putExtra("NOME_USUARIO", nomeUsuario)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        startActivity(intent)
        finish()
    }
}