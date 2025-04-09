package com.example.fitunifor.aluno

import com.example.fitunifor.aluno.MeusTreinosActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fitunifor.R
import android.widget.Button

class PrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val cardMeusTreinos = findViewById<CardView>(R.id.card_meus_treinos)
        val buttonIniciarTreino = findViewById<Button>(R.id.button_iniciar_treino1) // Adicione esta linha

        cardMeusTreinos.setOnClickListener {
            navigateToMeusTreinos()
        }

        buttonIniciarTreino.setOnClickListener {
            navigateToTreinoIniciado()
        }
    }

    private fun navigateToMeusTreinos() {
        try {
            val intent = Intent(this, MeusTreinosActivity::class.java).apply {
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

    private fun navigateToTreinoIniciado() {
        try {
            val intent = Intent(this, TreinoIniciadoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Não foi possível iniciar o treino\n${e.localizedMessage}",
                Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}