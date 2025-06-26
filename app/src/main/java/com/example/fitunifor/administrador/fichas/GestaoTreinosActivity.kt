package com.example.fitunifor.administrador.fichas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GestaoTreinosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TreinoAdapterAdmin
    private val listaTreinos = mutableListOf<Treino>()
    private lateinit var alunoAtual: Aluno
    private val db = Firebase.firestore

    companion object {
        private const val REQUEST_CODE_NOVO_TREINO = 1001
        private const val REQUEST_CODE_EDITAR_TREINO = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestao_treinos)

        alunoAtual = Aluno(
            intent.getStringExtra("aluno_id") ?: "",
            intent.getStringExtra("aluno_nome") ?: "Aluno",
            intent.getStringExtra("aluno_email") ?: ""
        )

        findViewById<TextView>(R.id.text_nome_aluno).text = alunoAtual.nome

        recyclerView = findViewById(R.id.recycler_view_treinos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TreinoAdapterAdmin(
            listaTreinos,
            onEditarClick = { treino -> abrirTelaEdicaoTreino(treino) },
            onRemoverClick = { treino -> removerTreino(treino) }
        )
        recyclerView.adapter = adapter

        findViewById<CardView>(R.id.card_novo_treino).setOnClickListener {
            abrirTelaNovoTreino()
        }

        findViewById<ImageView>(R.id.icon_back_painel_administrativo).setOnClickListener {
            finish()
        }

        carregarTreinosDoAluno()
    }

    private fun carregarTreinosDoAluno() {
        lifecycleScope.launch {
            try {
                val querySnapshot = db.collection("treinos")
                    .whereEqualTo("alunoId", alunoAtual.id)
                    .get()
                    .await()

                listaTreinos.clear()
                listaTreinos.addAll(querySnapshot.documents.map { doc ->
                    val exercicios = (doc.get("exercicios") as? List<Map<String, Any>>)?.map {
                        Exercicio(
                            id = it["id"] as? String ?: "",
                            nome = it["nome"] as? String ?: "",
                            grupoMuscular = it["grupoMuscular"] as? String ?: "",
                            imagemUrl = it["imagemUrl"] as? String,
                            videoUrl = it["videoUrl"] as? String,
                            series = (it["series"] as? List<Map<String, Any>>)?.map { serie ->
                                Serie(
                                    numero = (serie["numero"] as? Long)?.toInt() ?: 0,
                                    peso = serie["peso"] as? Double ?: 0.0,
                                    repeticoes = (serie["repeticoes"] as? Long)?.toInt() ?: 0
                                )
                            }?.toMutableList() ?: mutableListOf()
                        )
                    } ?: emptyList()

                    Treino(
                        id = doc.id,
                        alunoId = doc.getString("alunoId") ?: "",
                        titulo = doc.getString("titulo") ?: "",
                        diaDaSemana = doc.getString("diaDaSemana") ?: "",
                        imagemTreino = determinarImagemPadrao(exercicios),
                        exercicios = exercicios
                    )
                })

                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestaoTreinosActivity,
                    "Erro ao carregar treinos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun determinarImagemPadrao(exercicios: List<Exercicio>): String {
        if (exercicios.isEmpty()) return "treino"

        val gruposMusculares = exercicios.map { it.grupoMuscular?.lowercase() ?: "" }

        return when {
            gruposMusculares.any { it.contains("peito") } -> "treino_peito"
            gruposMusculares.any { it.contains("costas") } -> "treino_costas"
            gruposMusculares.any { it.contains("perna") || it.contains("quadríceps") || it.contains("posterior") } -> "treino_perna"
            else -> "treino"
        }
    }

    private fun abrirTelaNovoTreino() {
        val intent = Intent(this, NovoTreinoAlunoActivity::class.java).apply {
            putExtra("aluno_id", alunoAtual.id)
        }
        startActivityForResult(intent, REQUEST_CODE_NOVO_TREINO)
    }

    private fun abrirTelaEdicaoTreino(treino: Treino) {
        val intent = Intent(this, NovoTreinoAlunoActivity::class.java).apply {
            putExtra(NovoTreinoAlunoActivity.EXTRA_TREINO_EDICAO, treino)
            putExtra("aluno_id", alunoAtual.id)
        }
        startActivityForResult(intent, REQUEST_CODE_EDITAR_TREINO)
    }

    private fun removerTreino(treino: Treino) {
        lifecycleScope.launch {
            try {
                db.collection("treinos").document(treino.id).delete().await()
                listaTreinos.remove(treino)
                adapter.notifyDataSetChanged()
                Toast.makeText(
                    this@GestaoTreinosActivity,
                    "Treino removido",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestaoTreinosActivity,
                    "Erro ao remover: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_NOVO_TREINO -> {
                    data?.getParcelableExtra<Treino>("treino_salvo")?.let { novoTreino ->
                        // Determinar a imagem correta para o novo treino
                        novoTreino.imagemTreino = determinarImagemPadrao(novoTreino.exercicios)
                        listaTreinos.add(novoTreino)
                        adapter.notifyItemInserted(listaTreinos.size - 1)
                    }
                }
                REQUEST_CODE_EDITAR_TREINO -> {
                    data?.getParcelableExtra<Treino>(NovoTreinoAlunoActivity.EXTRA_TREINO_ATUALIZADO)?.let { treinoAtualizado ->
                        // Atualizar a imagem se necessário
                        treinoAtualizado.imagemTreino = determinarImagemPadrao(treinoAtualizado.exercicios)
                        val index = listaTreinos.indexOfFirst { it.id == treinoAtualizado.id }
                        if (index != -1) {
                            listaTreinos[index] = treinoAtualizado
                            adapter.notifyItemChanged(index)
                        }
                    }
                }
            }
        }
    }
}