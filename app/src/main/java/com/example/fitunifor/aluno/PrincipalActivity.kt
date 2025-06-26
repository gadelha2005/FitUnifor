package com.example.fitunifor.aluno

import Aula
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.MainActivity
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Treino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import android.graphics.PorterDuff
import android.view.Gravity
import android.widget.LinearLayout
import com.google.firebase.firestore.toObject
import kotlin.math.sin

class PrincipalActivity : AppCompatActivity() {

    private var pulseAnimator: ValueAnimator? = null
    private var currentLevel: String = ""

    private val db = Firebase.firestore

    private lateinit var recyclerAulas: RecyclerView
    private lateinit var recyclerTreinos: RecyclerView
    private lateinit var adapterAulas: AulasAdapterAluno
    private lateinit var adapterTreinos: TreinoAdapterAluno
    private lateinit var textTreinosCompletos: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)



        sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setupViews()
        setupNomeAluno()
        setupRecyclerViews()
        setupClickListeners()

        verificarEResetarInscricoes()
        carregarAulasDoFirebase()
        carregarTreinosDoDia()
        carregarTreinosCompletos()
        animateWaveEffect()
        setupMiniCalendario()
    }

    private fun setupViews() {
        recyclerAulas = findViewById(R.id.recycler_aulas_diarias)
        recyclerTreinos = findViewById(R.id.recycler_treino_do_dia)
        textTreinosCompletos = findViewById(R.id.text_treinos_completos)
    }

    private fun setupRecyclerViews() {
        // Configura o RecyclerView para aulas
        recyclerAulas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterAulas = AulasAdapterAluno(emptyList())
        recyclerAulas.adapter = adapterAulas

        recyclerTreinos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Adapter inicial vazio; recebe dados após a consulta ao Firestore
        adapterTreinos = TreinoAdapterAluno(
            emptyList(),
            onItemClick = { treino -> navigateToTreinoIniciado(treino.id) },
            onButtonClick = { treino -> navigateToTreinoIniciado(treino.id) }
        )
        recyclerTreinos.adapter = adapterTreinos
    }

    private fun carregarTreinosDoDia() {
        val alunoId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val diaSemanaAtual = SimpleDateFormat("EEEE", Locale("pt", "BR"))
            .format(Calendar.getInstance().time)
            .replaceFirstChar { it.uppercase() }

        db.collection("treinos")
            .whereEqualTo("alunoId", alunoId)  // Filtra pelo aluno logado
            .whereEqualTo("diaDaSemana", diaSemanaAtual)  // Filtra pelo dia atual
            .get()
            .addOnSuccessListener { documents ->
                val treinos = documents.map { doc ->
                    doc.toObject<Treino>().copy(id = doc.id)
                }

                if (treinos.isEmpty()) {
                    Log.d("DEBUG_TREINO", "Nenhum treino encontrado para hoje")
                    // Você pode mostrar uma mensagem ou um treino padrão aqui
                }

                adapterTreinos = TreinoAdapterAluno(
                    treinos,
                    onItemClick = { treino -> navigateToTreinoIniciado(treino.id) },
                    onButtonClick = { treino -> navigateToTreinoIniciado(treino.id) }
                )
                recyclerTreinos.adapter = adapterTreinos
            }
            .addOnFailureListener { exception ->
                Log.e("PrincipalActivity", "Erro ao carregar treinos do dia", exception)
                Toast.makeText(this, "Erro ao carregar treinos do dia", Toast.LENGTH_SHORT).show()
            }
    }



    private fun verificarEResetarInscricoes() {
        val ultimoReset = sharedPref.getLong("ultimo_reset", 0)
        val hoje = Calendar.getInstance()

        if (hoje.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
            hoje.timeInMillis - ultimoReset > 24 * 60 * 60 * 1000) {

            resetarInscricoesNoFirestore()
            sharedPref.edit().putLong("ultimo_reset", hoje.timeInMillis).apply()
        }
    }

    private fun resetarInscricoesNoFirestore() {
        db.collection("aulas")
            .get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()
                for (document in documents) {
                    val aulaRef = db.collection("aulas").document(document.id)
                    batch.update(aulaRef,
                        "alunosMatriculados", 0,
                        "listaAlunos", emptyList<String>()
                    )
                }
                batch.commit()
                    .addOnSuccessListener {
                        Log.d("PrincipalActivity", "Inscrições resetadas com sucesso")
                        carregarAulasDoFirebase()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PrincipalActivity", "Erro ao resetar inscrições", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PrincipalActivity", "Erro ao buscar aulas para reset", e)
            }
    }

    private fun setupClickListeners() {
        findViewById<CardView>(R.id.card_meus_treinos).setOnClickListener { navigateToMeusTreinos() }
        findViewById<CardView>(R.id.card_calendario).setOnClickListener { navigateCalendario() }
        findViewById<CardView>(R.id.card_ia).setOnClickListener { navigateIa() }

        findViewById<ImageView>(R.id.icon_log_out).setOnClickListener { exibirDialogLogout() }
    }

    private fun carregarTreinosCompletos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("treinosFinalizados")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val count = documents.size()
                textTreinosCompletos.text = count.toString()
                updateUserLevelIndicator(count)
                Log.d("PrincipalActivity", "Treinos completos: $count")
            }
            .addOnFailureListener { exception ->
                Log.e("PrincipalActivity", "Erro ao carregar treinos completos", exception)
                textTreinosCompletos.text = "0"
                updateUserLevelIndicator(0)
            }
    }

    private fun determineLevel(completedWorkouts: Int): Pair<Int, String> {
        return when {
            completedWorkouts >= 50 -> Pair(R.color.level_master, "Monstro Parrudão")
            completedWorkouts >= 30 -> Pair(R.color.level_expert, "Bombado")
            completedWorkouts >= 20 -> Pair(R.color.level_advanced, "Fortinho")
            completedWorkouts >= 10 -> Pair(R.color.level_intermediate, "Iniciante")
            else -> Pair(R.color.level_beginner, "Novato")
        }
    }

    private fun updateUserLevelIndicator(completedWorkouts: Int) {
        val (colorRes, levelText) = determineLevel(completedWorkouts)
        val nivelText = findViewById<TextView>(R.id.text_nivel_aluno)
        nivelText.text = levelText

        if (levelText != currentLevel) {
            currentLevel = levelText
            animateLevelUp()
            startPulseAnimation(colorRes)
        }
    }

    private fun animateLevelUp() {
        val nivelIcon = findViewById<ImageView>(R.id.image_nivel_aluno)
        val jump = TranslateAnimation(0f, 0f, -50f, 0f).apply {
            duration = 300
            interpolator = OvershootInterpolator()
        }

        val scale = ScaleAnimation(
            1f, 1.2f, 1f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
        }

        val animationSet = AnimationSet(true).apply {
            addAnimation(scale)
            addAnimation(jump)
        }

        nivelIcon.startAnimation(animationSet)
    }

    private fun startPulseAnimation(baseColorRes: Int) {
        pulseAnimator?.cancel()

        val nivelIcon = findViewById<ImageView>(R.id.image_nivel_aluno)
        val baseColor = ContextCompat.getColor(this, baseColorRes)

        pulseAnimator = ValueAnimator.ofArgb(baseColor,
            Color.rgb(
                (Color.red(baseColor) * 1.2f).toInt().coerceAtMost(255),
                (Color.green(baseColor) * 1.2f).toInt().coerceAtMost(255),
                (Color.blue(baseColor) * 1.2f).toInt().coerceAtMost(255)
            ),
            baseColor
        ).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                nivelIcon.setColorFilter(
                    animator.animatedValue as Int,
                    PorterDuff.Mode.SRC_IN
                )
            }
            start()
        }
    }

    private fun exibirDialogLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair da conta")
            .setMessage("Deseja realmente sair?")
            .setPositiveButton("Sim") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun setupNomeAluno() {
        var nomeUsuario = intent.getStringExtra("NOME_USUARIO")

        if (nomeUsuario.isNullOrEmpty()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    nomeUsuario = document.getString("nome")?.replace("\"", "") ?: "Aluno"
                    atualizarNome(nomeUsuario)
                }
                .addOnFailureListener { e ->
                    Log.e("PrincipalActivity", "Erro ao buscar nome", e)
                    atualizarNome("Aluno")
                }
        } else {
            nomeUsuario = nomeUsuario!!.replace("\"", "")
            atualizarNome(nomeUsuario)
        }
    }

    private fun atualizarNome(nome: String?) {
        findViewById<TextView>(R.id.text_nome_aluno).text = "Olá, ${nome ?: "Aluno"}"
    }

    private fun carregarAulasDoFirebase() {
        val diaAtual = getDiaSemanaAtual()
        Log.d("PrincipalActivity", "Carregando aulas para: $diaAtual")

        db.collection("aulas")
            .whereEqualTo("diaSemana", diaAtual)
            .get()
            .addOnSuccessListener { result ->
                val listaAulas = mutableListOf<Aula>()
                for (document in result) {
                    val aula = document.toObject(Aula::class.java).apply {
                        id = document.id
                        alunosMatriculados = document.getLong("alunosMatriculados")?.toInt() ?: 0
                        Log.d("PrincipalActivity", "Aula: $nome - Dia: $diaSemana - Vagas: $alunosMatriculados/$maxAlunos")
                    }
                    listaAulas.add(aula)
                }
                adapterAulas.atualizarAulas(listaAulas)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar aulas: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("PrincipalActivity", "Erro ao carregar aulas", exception)
            }
    }

    private fun getDiaSemanaAtual(): String {
        val calendar = Calendar.getInstance()
        val diaFormatado = SimpleDateFormat("EEEE", Locale("pt", "BR")).format(calendar.time)
            .replaceFirstChar { it.uppercase() }
        Log.d("PrincipalActivity", "Dia atual: $diaFormatado")
        return diaFormatado
    }

    private fun navigateToMeusTreinos() {
        try {
            startActivity(Intent(this, MeusTreinosActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            showErrorToast("Erro ao abrir Meus Treinos", e)
        }
    }

    private fun navigateToTreinoIniciado(treinoId: String) {
        try {
            // Primeiro buscamos o treino completo no Firestore
            db.collection("treinos").document(treinoId).get()
                .addOnSuccessListener { document ->
                    val treino = document.toObject<Treino>()?.copy(id = document.id)
                    if (treino != null) {
                        val intent = Intent(this, TreinoIniciadoActivity::class.java).apply {
                            putExtra("TREINO", treino)
                        }
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    } else {
                        Toast.makeText(this, "Treino não encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar treino", Toast.LENGTH_SHORT).show()
                    Log.e("PrincipalActivity", "Erro ao carregar treino", e)
                }
        } catch (e: Exception) {
            showErrorToast("Erro ao iniciar treino", e)
        }
    }

    private fun navigateIa() {
        try {
            startActivity(Intent(this, IAActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            showErrorToast("Erro ao abrir IA", e)
        }
    }

    private fun navigateCalendario() {
        try {
            startActivity(Intent(this, CalendarioActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            showErrorToast("Erro ao abrir Calendário", e)
        }
    }

    private fun showErrorToast(message: String, exception: Exception) {
        Toast.makeText(this, "$message\n${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        exception.printStackTrace()
    }

    private fun animateWaveEffect() {
        val nivelIcon = findViewById<ImageView>(R.id.image_nivel_aluno)
        val waveAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animation ->
                val waveOffset = animation.animatedValue as Float
                nivelIcon.translationY = -10f * sin(waveOffset * Math.PI * 2).toFloat()
            }
        }
        waveAnimator.start()
    }

    private fun setupMiniCalendario() {
        val cardCalendario = findViewById<CardView>(R.id.card_calendario)
        val textMesAno = findViewById<TextView>(R.id.text_mes_ano)
        val layoutSemanaAtual = findViewById<LinearLayout>(R.id.layout_semana_atual)

        // Configurar data atual
        val calendar = Calendar.getInstance()
        val hoje = calendar.get(Calendar.DAY_OF_MONTH)
        val mesAno = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(calendar.time)
            .replaceFirstChar { it.uppercase() }
        textMesAno.text = mesAno

        // Obter os dias da semana atual
        val diasSemana = obterDiasDaSemanaAtual(calendar)

        // Limpar views existentes
        layoutSemanaAtual.removeAllViews()

        // Adicionar os dias da semana
        for (dia in diasSemana) {
            val textView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = Gravity.CENTER
                text = dia.first.toString()
                textSize = 14f

                if (dia.first == hoje) {
                    // Destacar o dia atual
                    setTextColor(Color.WHITE)
                    background = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.circle_background)
                } else {
                    setTextColor(Color.BLACK)
                }
            }

            layoutSemanaAtual.addView(textView)
        }

        // Configurar clique no calendário
        cardCalendario.setOnClickListener {
            val intent = Intent(this, CalendarioActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun obterDiasDaSemanaAtual(calendar: Calendar): List<Pair<Int, String>> {
        val diasSemana = mutableListOf<Pair<Int, String>>()
        val diaSemanaAtual = calendar.get(Calendar.DAY_OF_WEEK)

        // Voltar para o primeiro dia da semana (domingo)
        calendar.add(Calendar.DAY_OF_MONTH, -(diaSemanaAtual - Calendar.SUNDAY))

        // Adicionar todos os 7 dias da semana
        for (i in 0 until 7) {
            val dia = calendar.get(Calendar.DAY_OF_MONTH)
            val nomeDia = SimpleDateFormat("EE", Locale("pt", "BR")).format(calendar.time)
                .replaceFirstChar { it.uppercase() }

            diasSemana.add(Pair(dia, nomeDia))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return diasSemana
    }
}