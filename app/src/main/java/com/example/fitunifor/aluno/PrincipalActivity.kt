package com.example.fitunifor.aluno

import com.example.fitunifor.aluno.MeusTreinosActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fitunifor.R

class PrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val cardMeusTreinos = findViewById<CardView>(R.id.card_meus_treinos)

        cardMeusTreinos.setOnClickListener {
            navigateToMeusTreinos()
        }
    }

    private fun navigateToMeusTreinos() {
        try {
            val intent = Intent(this,MeusTreinosActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Não foi possível abrir Meus Treinos\n${e.localizedMessage}",
                Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}