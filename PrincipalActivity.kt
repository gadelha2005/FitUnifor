package com.example.fitunifor.aluno

import Aula
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.content.ContextCompat
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.view.animation.AnimationSet
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.sin
import android.graphics.PorterDuffColorFilter
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate



class PrincipalActivity : AppCompatActivity() {

    private var pulseAnimator: ValueAnimator? = null
    private var currentLevel: String = ""
    private var lastAnimatedLevel: String = ""
    private val db = Firebase.firestore
    private lateinit var recyclerAulas: RecyclerView
    private lateinit var adapter: AulasAdapterAluno
    private lateinit var textTreinosCompletos: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val textDataAtual = findViewById<TextView>(R.id.text_data_atual)
        val calendarioHoje = Calendar.getInstance()
        val formato = java.text.SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        val dataFormatada = formato.format(calendarioHoje.time)
        textDataAtual.text = dataFormatada.capitalize()
        gerarDiasSemana()

        sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setupNomeAluno()
        setupViews()
        setupAulasRecyclerView()
        setupClickListeners()

        verificarEResetarInscricoes() // Nova função adicionada
        carregarAulasDoFirebase()
        carregarTreinosCompletos()
        animateWaveEffect()
    }

    private fun verificarEResetarInscricoes() {
        val ultimoReset = sharedPref.getLong("ultimo_reset", 0)
        val hoje = Calendar.getInstance()

        // Verifica se é segunda-feira e se já passou da hora de reset
        if (hoje.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
            hoje.timeInMillis - ultimoReset > 24 * 60 * 60 * 1000) {

            resetarInscricoesNoFirestore()

            // Atualiza o último horário de reset
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
                        // Recarrega as aulas após o reset
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

    private fun setupViews() {
        recyclerAulas = findViewById(R.id.recycler_aulas_diarias)
        textTreinosCompletos = findViewById(R.id.text_treinos_completos)
    }

    private fun setupClickListeners() {
        findViewById<CardView>(R.id.card_meus_treinos).setOnClickListener { navigateToMeusTreinos() }
        findViewById<CardView>(R.id.card_calendario).setOnClickListener { navigateCalendario() }
        findViewById<CardView>(R.id.card_ia).setOnClickListener { navigateIa() }
        findViewById<Button>(R.id.button_iniciar_treino1).setOnClickListener { navigateToTreinoIniciado() }

        val logoutIcon = findViewById<ImageView>(R.id.icon_log_out)
        logoutIcon.setOnClickListener { exibirDialogLogout() }
    }

    private fun carregarTreinosCompletos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("treinosFinalizados")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val count = documents.size()
                textTreinosCompletos.text = count.toString()
                updateUserLevelIndicator(count) // Adicione esta linha
                Log.d("PrincipalActivity", "Treinos completos: $count")
            }
            .addOnFailureListener { exception ->
                Log.e("PrincipalActivity", "Erro ao carregar treinos completos", exception)
                textTreinosCompletos.text = "0"
                updateUserLevelIndicator(0) // Atualize para 0 em caso de erro
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

    private fun setupAulasRecyclerView() {
        recyclerAulas.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = AulasAdapterAluno(emptyList())
        recyclerAulas.adapter = adapter
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
                adapter.atualizarAulas(listaAulas)
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

    private fun navigateToTreinoIniciado() {
        try {
            startActivity(Intent(this, TreinoIniciadoActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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

    private fun gerarDiasSemana() {
        val diasLayout = findViewById<LinearLayout>(R.id.diasSemanaLayout)
        diasLayout.removeAllViews()

        // Obtenho hoje e dia da semana (1=Domingo…7=Sábado)
        val hoje = Calendar.getInstance()
        val diaDaSemanaHoje = hoje.get(Calendar.DAY_OF_WEEK)

        // Calculo o domingo da semana atual
        val inicioSemana = hoje.clone() as Calendar
        inicioSemana.add(Calendar.DAY_OF_MONTH, -(diaDaSemanaHoje - Calendar.SUNDAY))

        // Para cada um dos 7 dias (Dom → Sáb)
        for (i in 0..6) {
            val diaCal = inicioSemana.clone() as Calendar
            diaCal.add(Calendar.DAY_OF_MONTH, i)
            val numeroDia = diaCal.get(Calendar.DAY_OF_MONTH)

            // Verifico se é hoje
            val isHoje = diaCal.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)
                    && diaCal.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR)

            // Crio o TextView só com o número do dia
            val tv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                gravity = Gravity.CENTER
                text = numeroDia.toString()
                textSize = 14f                           // mesmo tamanho dos labels estáticos
                setTextColor(Color.parseColor("#6C757D")) // mesma cor dos labels estáticos
                setPadding(4, 12, 4, 12)

                if (isHoje) {
                    // círculo de destaque e texto branco
                    background = ContextCompat.getDrawable(context, R.drawable.circle_background)
                    setTextColor(Color.WHITE)
                }
            }

            diasLayout.addView(tv)
        }
    }


    private fun showErrorToast(message: String, exception: Exception) {
        Toast.makeText(this, "$message\n${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        exception.printStackTrace()
    }

    private fun determineLevel(completedWorkouts: Int): Pair<Int, String> {
        return when {
            completedWorkouts >= 50 -> Pair(R.color.level_master, "Monstro Parrudão")
            completedWorkouts >= 30 -> Pair(R.color.level_expert, "Bombado")
            completedWorkouts >= 20 -> Pair(R.color.level_advanced, "Fortinho")
            completedWorkouts >= 10 -> Pair(R.color.level_intermediate, "Iniciante")
            completedWorkouts >= 0 -> Pair(R.color.level_beginner, "Novato")
            else -> Pair(R.color.gray, "Novato")
        }
    }

    private fun updateUserLevelIndicator(completedWorkouts: Int) {
        val (colorRes, levelText) = determineLevel(completedWorkouts)

        val nivelText = findViewById<TextView>(R.id.text_nivel_aluno)
        nivelText.text = levelText

        // Se o nível mudou
        if (levelText != currentLevel) {
            currentLevel = levelText
            animateLevelUp() // Sua animação atual de mudança de nível
            startPulseAnimation(colorRes) // Inicia a pulsação contínua
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
        // Cancela animação anterior se existir
        pulseAnimator?.cancel()

        val nivelIcon = findViewById<ImageView>(R.id.image_nivel_aluno)
        val baseColor = ContextCompat.getColor(this, baseColorRes)

        // Cria variações de tonalidade (mais claro e mais escuro)
        val hsl = FloatArray(3)
        Color.colorToHSV(baseColor, hsl)

        hsl[2] = (hsl[2] * 1.2f).coerceAtMost(1f) // 20% mais claro
        val lighterColor = Color.HSVToColor(hsl)

        hsl[2] = (hsl[2] * 0.8f).coerceAtLeast(0f) // 20% mais escuro
        val darkerColor = Color.HSVToColor(hsl)

        // Configura a animação
        pulseAnimator = ValueAnimator.ofArgb(baseColor, lighterColor, darkerColor, baseColor).apply {
            duration = 1500 // 1.5 segundos por ciclo
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

    private fun animateLevelIcon(targetColorRes: Int) {
        val nivelIcon = findViewById<ImageView>(R.id.image_nivel_aluno)
        val targetColor = ContextCompat.getColor(this, targetColorRes)

        // 1. Define uma cor inicial segura (cinza)
        val initialColor = ContextCompat.getColor(this, R.color.gray)

        // 2. Animação de cor (simples e direta)
        ValueAnimator.ofArgb(initialColor, targetColor).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                nivelIcon.setColorFilter(
                    animator.animatedValue as Int,
                    PorterDuff.Mode.SRC_IN
                )
            }
        }.start()

        // 3. Animação de pulsação
        nivelIcon.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(500)
            .withEndAction {
                nivelIcon.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .start()
            }
            .start()
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

        // Para parar a animação posteriormente:
        // waveAnimator.cancel()
    }

}