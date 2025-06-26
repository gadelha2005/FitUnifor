package com.example.fitunifor

import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.example.fitunifor.databinding.ActivityRedefinirSenhaBinding
//import com.google.firebase.auth.FirebaseAuth
//
class RedefinirSenhaActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityRedefinirSenhaBinding
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRedefinirSenhaBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        firebaseAuth = FirebaseAuth.getInstance()
//
//        val actionCode = intent.data?.getQueryParameter("oobCode")
//        if (actionCode == null) {
//            Toast.makeText(this, "Link inválido ou expirado", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
//
//        binding.buttonConfirmarNovaSenha.setOnClickListener {
//            val novaSenha = binding.textNovaSenha.text.toString()
//            val confirmacaoSenha = binding.textConfirmarNovaSenha.text.toString()
//
//            when {
//                novaSenha.isEmpty() -> {
//                    binding.textNovaSenha.error = "Digite a nova senha"
//                    binding.textNovaSenha.requestFocus()
//                }
//                novaSenha.length < 6 -> {
//                    binding.textNovaSenha.error = "Senha deve ter pelo menos 6 caracteres"
//                    binding.textNovaSenha.requestFocus()
//                }
//                confirmacaoSenha.isEmpty() -> {
//                    binding.textConfirmarNovaSenha.error = "Confirme a nova senha"
//                    binding.textConfirmarNovaSenha.requestFocus()
//                }
//                novaSenha != confirmacaoSenha -> {
//                    binding.textConfirmarNovaSenha.error = "As senhas não coincidem"
//                }
//                else -> {
//                    alterarSenha(actionCode, novaSenha)
//                }
//            }
//        }
//    }
//
//    private fun alterarSenha(actionCode: String, novaSenha: String) {
//        firebaseAuth.confirmPasswordReset(actionCode, novaSenha)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(
//                        this,
//                        "Senha alterada com sucesso!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    voltarParaLogin()
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Erro ao alterar senha: ${task.exception?.message}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//    }
//
//    private fun voltarParaLogin() {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            putExtra("SHOW_LOGIN_TAB", true)
//        }
//        startActivity(intent)
//        finish()
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//    }
}