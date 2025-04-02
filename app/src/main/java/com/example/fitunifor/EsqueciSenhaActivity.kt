package com.example.fitunifor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EsqueciSenhaActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueci_senha)

        // Botão Voltar ao Login
        findViewById<Button>(R.id.button_voltar_login).setOnClickListener {
            finish() // Fecha a activity atual e volta para a anterior
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Botão Redefinir Senha - NOVA IMPLEMENTAÇÃO
        findViewById<Button>(R.id.button_enviar_para_email).setOnClickListener {
            val intent = Intent(this, RedefinirSenhaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}