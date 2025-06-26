package com.example.fitunifor.administrador.fichas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NovoTreinoAlunoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExercicioNoTreinoAdapter
    private val exerciciosAdicionados = mutableListOf<Exercicio>()
    private var treinoEditando: Treino? = null
    private lateinit var alunoId: String
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val REQUEST_CODE_ADICIONAR_EXERCICIO = 1001
        const val EXTRA_TREINO_EDICAO = "treino_edicao"
        const val EXTRA_TREINO_ATUALIZADO = "treino_atualizado"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_treino_aluno)

        alunoId = intent.getStringExtra("aluno_id") ?: ""

        recyclerView = findViewById(R.id.recyclerViewExerciciosTreino)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExercicioNoTreinoAdapter(
            exerciciosAdicionados,
            onRemoverExercicio = { exercicio ->
                exerciciosAdicionados.remove(exercicio)
                adapter.notifyDataSetChanged()
            },
            onAdicionarSerie = { exercicio ->
                exercicio.adicionarSerie()
                adapter.notifyDataSetChanged()
            }
        )
        recyclerView.adapter = adapter

        treinoEditando = intent.getParcelableExtra(EXTRA_TREINO_EDICAO)
        if (treinoEditando != null) {
            carregarTreinoExistente(treinoEditando!!)
            findViewById<TextView>(R.id.text_salvar_treino).text = "Salvar"
            findViewById<TextView>(R.id.textView46).text = "Editar Treino"
        }

        findViewById<TextView>(R.id.text_salvar_treino).setOnClickListener {
            salvarTreino()
        }

        findViewById<ImageView>(R.id.icon_back_gestao_treinos).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.button_adicionar_exercicios_treino).setOnClickListener {
            val intent = Intent(this, AdicionarExercicioActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADICIONAR_EXERCICIO)
        }
    }

    private fun carregarTreinoExistente(treino: Treino) {
        findViewById<EditText>(R.id.editTextText3).setText(treino.titulo)
        exerciciosAdicionados.clear()
        exerciciosAdicionados.addAll(treino.exercicios)
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADICIONAR_EXERCICIO && resultCode == Activity.RESULT_OK) {
            data?.getParcelableArrayListExtra<Exercicio>("exercicios_selecionados")?.let {
                it.forEach { novoExercicio ->
                    if (!exerciciosAdicionados.any { e -> e.id == novoExercicio.id }) {
                        exerciciosAdicionados.add(novoExercicio)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun validarTreino(): Boolean {
        if (exerciciosAdicionados.isEmpty()) {
            Toast.makeText(this, "Adicione pelo menos um exercício", Toast.LENGTH_SHORT).show()
            return false
        }

        for (exercicio in exerciciosAdicionados) {
            if (exercicio.series.isEmpty()) {
                Toast.makeText(this, "${exercicio.nome}: Adicione pelo menos uma série", Toast.LENGTH_SHORT).show()
                return false
            }

            for (serie in exercicio.series) {
                if (serie.peso <= 0 || serie.repeticoes <= 0) {
                    Toast.makeText(this,
                        "${exercicio.nome}: Série ${serie.numero} inválida (Peso: ${serie.peso}, Reps: ${serie.repeticoes})",
                        Toast.LENGTH_LONG).show()
                    return false
                }
            }
        }
        return true
    }

    private fun salvarTreino() {
        val titulo = findViewById<EditText>(R.id.editTextText3).text.toString()

        if (titulo.isEmpty()) {
            Toast.makeText(this, "Digite um título para o treino", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validarTreino()) return

        if (treinoEditando != null) {
            atualizarTreinoExistente(titulo)
        } else {
            mostrarDialogSelecaoDia(titulo)
        }
    }

    private fun atualizarTreinoExistente(titulo: String) {
        val treinoAtualizado = treinoEditando!!.copy(
            titulo = titulo,
            exercicios = ArrayList(exerciciosAdicionados)
        )

        lifecycleScope.launch {
            try {
                db.collection("treinos").document(treinoAtualizado.id)
                    .set(converterTreinoParaMap(treinoAtualizado))
                    .await()

                val resultIntent = Intent().apply {
                    putExtra(EXTRA_TREINO_ATUALIZADO, treinoAtualizado)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@NovoTreinoAlunoActivity,
                    "Erro ao atualizar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarDialogSelecaoDia(titulo: String) {
        SelecionarDiaTreinoDialogFragment().apply {
            // Correção: Usar a interface corretamente
            setOnDiaSelecionadoListener(object : SelecionarDiaTreinoDialogFragment.OnDiaSelecionadoListener {
                override fun onDiaSelecionado(dia: String) {
                    val treinoNovo = Treino(
                        id = db.collection("treinos").document().id,
                        alunoId = alunoId,
                        titulo = titulo,
                        diaDaSemana = dia, // Removido .toString() pois já é String
                        exercicios = ArrayList(exerciciosAdicionados)
                    )

                    salvarNovoTreino(treinoNovo)
                }
            })
        }.show(supportFragmentManager, "SelecionarDiaDialog")
    }

    private fun salvarNovoTreino(treino: Treino) {
        lifecycleScope.launch {
            try {
                db.collection("treinos").document(treino.id)
                    .set(converterTreinoParaMap(treino))
                    .await()

                val resultIntent = Intent().apply {
                    putExtra("treino_salvo", treino)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@NovoTreinoAlunoActivity,
                    "Erro ao salvar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun converterTreinoParaMap(treino: Treino): Map<String, Any> {
        return mapOf(
            "id" to treino.id,
            "alunoId" to treino.alunoId,
            "titulo" to treino.titulo,
            "diaDaSemana" to treino.diaDaSemana,
            "imagemTreino" to treino.imagemTreino,
            "exercicios" to treino.exercicios.map { exercicio ->
                mapOf(
                    "id" to exercicio.id,
                    "nome" to exercicio.nome,
                    "grupoMuscular" to exercicio.grupoMuscular,
                    "imagemUrl" to exercicio.imagemUrl,
                    "videoUrl" to exercicio.videoUrl,
                    "series" to exercicio.series.map { serie ->
                        mapOf(
                            "numero" to serie.numero,
                            "peso" to serie.peso,
                            "repeticoes" to serie.repeticoes
                        )
                    }
                )
            }
        )
    }
}