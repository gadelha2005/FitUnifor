package com.example.fitunifor.aluno

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.aluno.ExercicioFinalizadoAdapter
import com.example.fitunifor.model.TreinoFinalizado

class TreinoFinalizadoActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino_finalizado)

        val iconVoltar = findViewById<ImageView>(R.id.icon_back_historico_treinos)
        iconVoltar.setOnClickListener { finish() }

        val treino = intent.getParcelableExtra<TreinoFinalizado>(EXTRA_TREINO)
        treino?.let { exibirDadosTreino(it) }
    }

    private fun exibirDadosTreino(treino: TreinoFinalizado) {
        findViewById<TextView>(R.id.text_nome_treino).text = treino.titulo
        findViewById<TextView>(R.id.text_exercicios_feitos).text =
            "${treino.exerciciosCompletos} de ${treino.totalExercicios} exercícios"

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_exercicios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExercicioFinalizadoAdapter(treino.exercicios)
    }

    companion object {
        private const val EXTRA_TREINO = "EXTRA_TREINO"

        fun newIntent(context: Context, treino: TreinoFinalizado): Intent {
            return Intent(context, TreinoFinalizadoActivity::class.java).apply {
                putExtra(EXTRA_TREINO, treino)
            }
        }
    }
}