package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fitunifor.R

class PrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        // Encontrar o CardView pelo ID
        val cardMeusTreinos = findViewById<CardView>(R.id.card_meus_treinos)

        // Configurar o clique no CardView
        cardMeusTreinos.setOnClickListener {
            val intent = Intent(this, com.example.fitunifor.aluno.MeusTreinosActivity::class.java)
            startActivity(intent)

            // Opcional: Adicione animação
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}