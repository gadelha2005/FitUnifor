package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R

class MeusTreinosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_treinos)

        // Configurar clique na ImageView de voltar
        findViewById<ImageView>(R.id.icon_arrow_back_meus_treinos).setOnClickListener {
            navigateBackToPrincipal()
        }
    }

    private fun navigateBackToPrincipal() {
        // Opção 1: Simplesmente finaliza a activity atual
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        // OU Opção 2: Se precisar de um controle mais refinado:
        /*
        val intent = Intent(this, PrincipalActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        */
    }
}