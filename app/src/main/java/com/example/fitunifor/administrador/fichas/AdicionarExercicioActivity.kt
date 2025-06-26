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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import kotlinx.coroutines.launch

class AdicionarExercicioActivity : AppCompatActivity(), FiltroMusculoDialogFragment.OnFiltroAplicadoListener {

    private lateinit var adapter: ExercicioAdapter
    private val exerciciosSelecionados = mutableListOf<Exercicio>()
    private lateinit var btnAdicionar: CardView
    private lateinit var textQuantidade: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var textMusculo: TextView
    private lateinit var exercicioRepository: ExercicioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_exercicio)

        initViews()
        setupRecyclerView()
        setupListeners()
        carregarExercicios()
    }

    private fun initViews() {
        btnAdicionar = findViewById(R.id.btn_adicionar_exercicios)
        textQuantidade = findViewById(R.id.text_quantidade_exercicios)
        recyclerView = findViewById(R.id.recyclerViewExercicios)
        textMusculo = findViewById(R.id.text_musculo)
        exercicioRepository = ExercicioRepository()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExercicioAdapter(emptyList()) { exercicio, isChecked ->
            if (isChecked) exerciciosSelecionados.add(exercicio)
            else exerciciosSelecionados.remove(exercicio)
            atualizarBotaoAdicionar()
        }
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<EditText>(R.id.edit_text_buscar_exercicio).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    adapter.filter(s?.toString() ?: "")
                }
            }
        )

        findViewById<ImageView>(R.id.icon_back_novo_treino_aluno).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btnAdicionar.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putParcelableArrayListExtra("exercicios_selecionados", ArrayList(adapter.getSelecionados()))
            })
            finish()
        }

        findViewById<CardView>(R.id.card_filtro_musculos).setOnClickListener {
            FiltroMusculoDialogFragment().apply {
                setOnFiltroAplicadoListener(this@AdicionarExercicioActivity)
            }.show(supportFragmentManager, "FiltroMusculosDialog")
        }
    }

    private fun carregarExercicios() {
        lifecycleScope.launch {
            try {
                val exercicios = exercicioRepository.getExercicios()
                adapter.atualizarLista(exercicios)
            } catch (e: Exception) {
                Toast.makeText(
                    this@AdicionarExercicioActivity,
                    "Erro ao carregar exercícios",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun atualizarBotaoAdicionar() {
        btnAdicionar.visibility = if (exerciciosSelecionados.isNotEmpty()) View.VISIBLE else View.GONE
        textQuantidade.text = "${exerciciosSelecionados.size} Exercício(s)"
    }

    override fun onFiltroAplicado(musculos: List<String>) {
        textMusculo.text = if (musculos.isEmpty()) "Todos os músculos"
        else musculos.joinToString()
        adapter.setFiltroMusculos(musculos)
    }
}