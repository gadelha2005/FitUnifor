package com.example.fitunifor.aluno

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.model.TreinoFinalizado

class TreinoFinalizadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino_finalizado)

        // Configura botão de voltar
        val iconVoltar = findViewById<ImageView>(R.id.icon_back_historico_treinos)
        iconVoltar.setOnClickListener { voltarHistoricoTreinos() }

        // Recebe os dados do treino
        val treino = intent.getParcelableExtra<TreinoFinalizado>("TREINO_FINALIZADO")
        treino?.let { exibirDadosTreino(it) }
    }

    private fun exibirDadosTreino(treino: TreinoFinalizado) {
        // Preenche os dados do cabeçalho
        findViewById<TextView>(R.id.text_nome_treino).text = treino.titulo
        findViewById<TextView>(R.id.text_exercicios_feitos).text =
            "${treino.exerciciosCompletos} de ${treino.totalExercicios} exercícios"

        // Configura o RecyclerView de exercícios
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_exercicios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExercicioFinalizadoAdapter(treino.exercicios)
    }

    private fun voltarHistoricoTreinos() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}


