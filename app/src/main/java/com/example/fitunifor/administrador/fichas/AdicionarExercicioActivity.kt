package com.example.fitunifor.administrador.fichas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class AdicionarExercicioActivity : AppCompatActivity(), FiltroMusculoDialogFragment.OnFiltroAplicadoListener {

    private lateinit var adapter: ExercicioAdapter
    private val exerciciosSelecionados = mutableListOf<Exercicio>()
    private lateinit var btnAdicionar: CardView
    private lateinit var textQuantidade: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var textMusculo: TextView

    private val todosExercicios = listOf(
        Exercicio(1, "Supino Reto", "Peito"),
        Exercicio(2, "Agachamento Livre", "Pernas"),
        Exercicio(3, "Barra Fixa", "Costas"),
        Exercicio(4, "Levantamento Terra", "Costas/Pernas"),
        Exercicio(5, "Rosca Direta", "Bíceps"),
        Exercicio(6, "Tríceps Corda", "Tríceps"),
        Exercicio(7, "Leg Press", "Pernas"),
        Exercicio(8, "Crucifixo", "Peito"),
        Exercicio(9, "Puxada Alta", "Costas")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_exercicio)

        // Inicializa views
        btnAdicionar = findViewById(R.id.btn_adicionar_exercicios)
        textQuantidade = findViewById(R.id.text_quantidade_exercicios)
        recyclerView = findViewById(R.id.recyclerViewExercicios)
        textMusculo = findViewById(R.id.text_musculo)

        setupRecyclerView()
        setupBusca()
        setupBotaoVoltar()
        setupBotaoAdicionar()
        setupFiltroMusculos()
    }

    private fun setupFiltroMusculos() {
        findViewById<CardView>(R.id.card_filtro_musculos).setOnClickListener {
            FiltroMusculoDialogFragment().apply {
                setOnFiltroAplicadoListener(this@AdicionarExercicioActivity)
            }.show(supportFragmentManager, "FiltroMusculosDialog")
        }
    }

    override fun onFiltroAplicado(musculos: List<String>) {
        textMusculo.text = if (musculos.isEmpty()) "Todos os músculos"
        else musculos.joinToString()
        adapter.setFiltroMusculos(musculos)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExercicioAdapter(todosExercicios) { exercicio, isChecked ->
            if (isChecked) exerciciosSelecionados.add(exercicio)
            else exerciciosSelecionados.remove(exercicio)
            atualizarBotaoAdicionar()
        }
        recyclerView.adapter = adapter
    }

    private fun atualizarBotaoAdicionar() {
        btnAdicionar.visibility = if (exerciciosSelecionados.isNotEmpty()) View.VISIBLE else View.GONE
        textQuantidade.text = "${exerciciosSelecionados.size} Exercício(s)"
    }

    private fun setupBusca() {
        findViewById<EditText>(R.id.edit_text_buscar_exercicio).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    adapter.filter(s?.toString() ?: "")
                }
            }
        )
    }

    private fun setupBotaoVoltar() {
        findViewById<ImageView>(R.id.icon_back_novo_treino_aluno).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun setupBotaoAdicionar() {
        btnAdicionar.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putParcelableArrayListExtra("exercicios_selecionados", ArrayList(adapter.getSelecionados()))
            })
            finish()
        }
    }
}