package com.example.fitunifor.aluno

import Aula
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Treino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarioActivity : AppCompatActivity() {

    private lateinit var btnTreino: ToggleButton
    private lateinit var btnAula: ToggleButton
    private lateinit var mesAnoTextView: TextView
    private lateinit var gridDias: GridLayout
    private lateinit var dataSelecionadaTextView: TextView
    private lateinit var recyclerTreinos: RecyclerView
    private lateinit var recyclerAulas: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usuarioId = auth.currentUser?.uid ?: ""

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
    private val displayDateFormat =
        SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

    private var diaSelecionado: Int = -1
    private var mesSelecionado: Int = calendar.get(Calendar.MONTH)
    private var anoSelecionado: Int = calendar.get(Calendar.YEAR)

    private lateinit var treinosAdapter: TreinoAdapterAluno
    private lateinit var aulasAdapter: AulasAdapterAluno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        inicializarViews()
        configurarListeners()
        atualizarMesAno()
        popularCalendario()

        // Configurar RecyclerViews
        recyclerTreinos.layoutManager = LinearLayoutManager(this)
        recyclerAulas.layoutManager = LinearLayoutManager(this)

        treinosAdapter = TreinoAdapterAluno(
            emptyList(),
            { treino -> mostrarDetalhesTreino(treino) },
            { treino -> iniciarTreino(treino) }
        )

        aulasAdapter = AulasAdapterAluno(emptyList())

        recyclerTreinos.adapter = treinosAdapter
        recyclerAulas.adapter = aulasAdapter

        // Selecionar o dia atual por padrão
        selecionarDia(calendar.get(Calendar.DAY_OF_MONTH))
    }

    private fun inicializarViews() {
        btnTreino = findViewById(R.id.button_treino_diario)
        btnAula = findViewById(R.id.button_aula_diaria)
        mesAnoTextView = findViewById(R.id.mes_ano)
        gridDias = findViewById(R.id.grid_dias)
        dataSelecionadaTextView = findViewById(R.id.text_data_selecionada)
        recyclerTreinos = findViewById(R.id.recycler_treinos)
        recyclerAulas = findViewById(R.id.recycler_aulas)

        findViewById<ImageView>(R.id.icon_back_principal).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun configurarListeners() {
        btnTreino.setOnCheckedChangeListener { _, _ -> atualizarExibicao() }
        btnAula.setOnCheckedChangeListener { _, _ -> atualizarExibicao() }
    }

    private fun atualizarMesAno() {
        mesAnoTextView.text = dateFormat.format(calendar.time).replaceFirstChar {
            it.titlecase(Locale.getDefault())
        }
    }

    private fun popularCalendario() {
        gridDias.removeAllViews()

        val tamanhoCelula = resources.getDimensionPixelSize(R.dimen.tamanho_celula_calendario)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val primeiroDiaSemana = calendar.get(Calendar.DAY_OF_WEEK)
        val diasNoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1 until primeiroDiaSemana) addEmptyDayToGrid(tamanhoCelula)
        for (dia in 1..diasNoMes) addDayToGrid(dia, tamanhoCelula)
    }

    private fun addEmptyDayToGrid(tamanhoCelula: Int) {
        val view = View(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = tamanhoCelula
                height = tamanhoCelula
            }
        }
        gridDias.addView(view)
    }

    private fun addDayToGrid(dia: Int, tamanhoCelula: Int) {
        val cardView =
            layoutInflater.inflate(R.layout.item_dia_calendario, gridDias, false) as CardView
        val textDia = cardView.findViewById<TextView>(R.id.text_dia)

        cardView.layoutParams = GridLayout.LayoutParams().apply {
            width = tamanhoCelula
            height = tamanhoCelula
            setMargins(8, 8, 8, 8)
        }

        textDia.text = dia.toString()
        textDia.textSize = 16f

        val hoje = Calendar.getInstance()
        if (dia == hoje.get(Calendar.DAY_OF_MONTH) &&
            mesSelecionado == hoje.get(Calendar.MONTH) &&
            anoSelecionado == hoje.get(Calendar.YEAR)
        ) {
            cardView.setCardBackgroundColor(Color.parseColor("#E9F7FF"))
            textDia.setTextColor(Color.parseColor("#007BFF"))
        }

        if (dia == diaSelecionado) {
            cardView.setCardBackgroundColor(Color.parseColor("#007BFF"))
            textDia.setTextColor(Color.WHITE)
        }

        cardView.setOnClickListener { selecionarDia(dia) }
        gridDias.addView(cardView)
    }

    private fun selecionarDia(dia: Int) {
        diaSelecionado = dia
        dataSelecionadaTextView.text = getDataFormatada(dia)
        popularCalendario()
        carregarDadosDoDia()
    }

    private fun getDataFormatada(dia: Int): String {
        val cal = Calendar.getInstance().apply { set(anoSelecionado, mesSelecionado, dia) }
        return displayDateFormat.format(cal.time).replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

    private fun carregarDadosDoDia() {
        if (diaSelecionado == -1) return

        val diaSemana = when (Calendar.getInstance().apply {
            set(anoSelecionado, mesSelecionado, diaSelecionado)
        }.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Segunda-feira"
            Calendar.TUESDAY -> "Terça-feira"
            Calendar.WEDNESDAY -> "Quarta-feira"
            Calendar.THURSDAY -> "Quinta-feira"
            Calendar.FRIDAY -> "Sexta-feira"
            Calendar.SATURDAY -> "Sábado"
            else -> ""
        }

        if (btnTreino.isChecked) carregarTreinosDoDia(diaSemana)
        if (btnAula.isChecked) carregarAulasDoDia(diaSemana)
    }

    private fun carregarTreinosDoDia(diaSemana: String) {
        val alunoId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("treinos")
            .whereEqualTo("alunoId", alunoId)
            .whereEqualTo("diaDaSemana", diaSemana)
            .get()
            .addOnSuccessListener { documents ->
                val treinos = documents.map { doc -> doc.toObject<Treino>().copy(id = doc.id) }

                if (treinos.isEmpty()) {
                    Toast.makeText(this, "Nenhum treino encontrado para este dia", Toast.LENGTH_SHORT).show()
                }

                treinosAdapter = TreinoAdapterAluno(
                    treinos,
                    onItemClick = { treino -> mostrarDetalhesTreino(treino) },
                    onButtonClick = { treino -> iniciarTreino(treino) }
                )
                recyclerTreinos.adapter = treinosAdapter
                atualizarExibicao()
            }
            .addOnFailureListener { exception ->
                Log.e("CalendarioActivity", "Erro ao carregar treinos", exception)
                Toast.makeText(this, "Erro ao carregar treinos", Toast.LENGTH_SHORT).show()
            }
    }

    /** -----------------------------------------------------------------------
     *  MÉTODO ALTERADO: agora traz TODAS as aulas do dia selecionado,
     *  sem filtrar se o aluno está matriculado ou não.
     *  -------------------------------------------------------------------- */
    private fun carregarAulasDoDia(diaSemana: String) {
        Log.d("CalendarioActivity", "Carregando aulas para: $diaSemana")

        db.collection("aulas")
            .whereEqualTo("diaSemana", diaSemana)
            .get()
            .addOnSuccessListener { result ->
                val listaAulas = result.map { document ->
                    document.toObject(Aula::class.java).apply { id = document.id }
                }

                if (listaAulas.isEmpty()) {
                    Toast.makeText(this, "Nenhuma aula encontrada para $diaSemana", Toast.LENGTH_SHORT).show()
                }

                aulasAdapter.atualizarAulas(listaAulas)
                atualizarExibicao()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar aulas: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("CalendarioActivity", "Erro ao carregar aulas", exception)
            }
    }
    // -----------------------------------------------------------------------

    @SuppressLint("NotifyDataSetChanged")
    private fun atualizarExibicao() {
        recyclerTreinos.visibility = if (btnTreino.isChecked) View.VISIBLE else View.GONE
        recyclerAulas.visibility = if (btnAula.isChecked) View.VISIBLE else View.GONE
    }

    private fun mostrarDetalhesTreino(treino: Treino) {
        Toast.makeText(this, "Detalhes do treino: ${treino.titulo}", Toast.LENGTH_SHORT).show()
    }

    private fun iniciarTreino(treino: Treino) {
        db.collection("treinos").document(treino.id).get()
            .addOnSuccessListener { document ->
                val treinoCompleto = document.toObject<Treino>()?.copy(id = document.id)
                if (treinoCompleto != null) {
                    val intent = Intent(this, TreinoIniciadoActivity::class.java).apply {
                        putExtra("TREINO", treinoCompleto)
                    }
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                } else {
                    Toast.makeText(this, "Treino não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar treino", Toast.LENGTH_SHORT).show()
                Log.e("CalendarioActivity", "Erro ao carregar treino", e)
            }
    }
}
