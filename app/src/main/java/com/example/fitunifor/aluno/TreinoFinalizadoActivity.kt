package com.example.fitunifor.aluno

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R

class TreinoFinalizadoActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino_finalizado)

        val iconVoltar = findViewById<ImageView>(R.id.icon_back_historico_treinos)

        iconVoltar.setOnClickListener{
            voltarHistoricoTreinos()
        }
    }

    private fun voltarHistoricoTreinos(){
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}


