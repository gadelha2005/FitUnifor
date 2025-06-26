package com.example.fitunifor.aluno

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Exercicio
import com.example.fitunifor.administrador.fichas.Treino
import com.example.fitunifor.model.TreinoFinalizado
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TreinoIniciadoActivity : AppCompatActivity() {

    private lateinit var iconVoltarTelaInicial: ImageView
    private lateinit var timerText: TextView
    private lateinit var startButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var resetButton: ImageView
    private lateinit var editTimerIcon: ImageView
    private lateinit var vibrator: Vibrator
    private lateinit var textExerciciosFeitos: TextView
    private lateinit var textNomeTreino: TextView
    private lateinit var buttonFinalizar: Button

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis: Long = 60000
    private var initialTimeInMillis: Long = 60000
    private var exerciciosConcluidos = 0
    private var totalExercicios = 0
    private lateinit var treinoAtual: Treino
    private val exerciciosMarcados = mutableSetOf<Int>()

    @SuppressLint("MissingInflatedId", "ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino_iniciado)

        // Inicializa o vibrator
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Inicializa componentes da interface
        iconVoltarTelaInicial = findViewById(R.id.icon_back_historico_treinos)
        timerText = findViewById(R.id.timer_text)
        startButton = findViewById(R.id.icon_start_timer)
        pauseButton = findViewById(R.id.icon_pause_timer)
        resetButton = findViewById(R.id.icon_reset_timer)
        editTimerIcon = findViewById(R.id.icon_edit_timer)
        textExerciciosFeitos = findViewById(R.id.text_exercicios_feitos)
        textNomeTreino = findViewById(R.id.text_nome_treino)
        buttonFinalizar = findViewById(R.id.button4)

        // Recebe o treino passado pela intent
        treinoAtual = intent.getParcelableExtra<Treino>("TREINO")!!
        textNomeTreino.text = treinoAtual.titulo
        totalExercicios = treinoAtual.exercicios.size

        setupRecyclerView(treinoAtual.exercicios)

        TreinoManager.iniciarTreino(treinoAtual)
        TreinoManager.setExerciciosConcluidos(exerciciosConcluidos)
        TreinoManager.setTimeLeft(timeLeftInMillis)

        updateTimerDisplay()
        updateExerciciosFeitosText()
        showStartButton()

        setupClickListeners()
    }

    private fun setupRecyclerView(exercicios: List<Exercicio>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_exercicios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ExercicioTreinoIniciadoAdapter(
            exercicios,
            onCheckChange = { position, isChecked ->
                if (isChecked) {
                    exerciciosMarcados.add(position)
                } else {
                    exerciciosMarcados.remove(position)
                }
                exerciciosConcluidos = exerciciosMarcados.size
                updateExerciciosFeitosText()
                TreinoManager.setExerciciosConcluidos(exerciciosConcluidos)
            },
            onPlayClick = { position ->
                val exercicio = exercicios[position]
                exercicio.videoUrl?.let { videoUrl ->
                    showVideoDialog(exercicio.nome, videoUrl)
                } ?: run {
                    Toast.makeText(this, "Vídeo não disponível para este exercício", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter
    }

    private fun showVideoDialog(title: String, videoFileName: String) {
        val dialog = ExampleDialogFragment.newInstance(title, videoFileName)
        dialog.show(supportFragmentManager, "ExampleDialogFragment")
    }

    private fun setupClickListeners() {
        iconVoltarTelaInicial.setOnClickListener {
            showConfirmationDialog()
        }

        startButton.setOnClickListener {
            startTimer()
            showPauseButton()
            TreinoManager.setTimeLeft(timeLeftInMillis)
        }

        pauseButton.setOnClickListener {
            pauseTimer()
            showStartButton()
            TreinoManager.setTimeLeft(timeLeftInMillis)
        }

        resetButton.setOnClickListener {
            resetTimer()
            showStartButton()
            TreinoManager.setTimeLeft(timeLeftInMillis)
        }

        editTimerIcon.setOnClickListener {
            countDownTimer?.cancel()
            timerRunning = false
            val originalTimeInSeconds = (initialTimeInMillis / 1000).toInt()
            val dialog = TimerDialogFragment(originalTimeInSeconds) { selectedTimeInSeconds ->
                initialTimeInMillis = selectedTimeInSeconds * 1000L
                timeLeftInMillis = initialTimeInMillis
                TreinoManager.setTimeLeft(timeLeftInMillis)
                updateTimerDisplay()
                showStartButton()
                Toast.makeText(this, "Tempo atualizado!", Toast.LENGTH_SHORT).show()
            }
            dialog.show(supportFragmentManager, "TimerDialog")
        }

        buttonFinalizar.setOnClickListener {
            salvarTreinoFinalizadoNoFirebase()
            TreinoManager.finalizarTreino()
            finish()
        }
    }

    private fun showConfirmationDialog() {
        val dialog = ConfirmacaoTreinoDialogFragment().apply {
            setListener(object : ConfirmacaoTreinoDialogFragment.ConfirmacaoTreinoListener {
                override fun onContinuarTreino() {}
                override fun onCancelarTreino() {
                    TreinoManager.finalizarTreino()
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })
        }
        dialog.show(supportFragmentManager, "ConfirmacaoTreinoDialog")
    }

    private fun updateExerciciosFeitosText() {
        textExerciciosFeitos.text = "$exerciciosConcluidos de $totalExercicios exercícios"
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
                TreinoManager.setTimeLeft(timeLeftInMillis)
            }
            override fun onFinish() {
                timerRunning = false
                vibrate()
                resetTimer()
                showStartButton()
            }
        }.start()
        timerRunning = true
    }

    private fun vibrate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1500)
        }
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = initialTimeInMillis
        updateTimerDisplay()
        timerRunning = false
        TreinoManager.setTimeLeft(timeLeftInMillis)
    }

    private fun updateTimerDisplay() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun showStartButton() {
        startButton.visibility = ImageView.VISIBLE
        pauseButton.visibility = ImageView.GONE
    }

    private fun showPauseButton() {
        startButton.visibility = ImageView.GONE
        pauseButton.visibility = ImageView.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        vibrator.cancel()
    }

    private fun salvarTreinoFinalizadoNoFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val exerciciosFinalizados = treinoAtual.exercicios.mapIndexed { index, exercicio ->
            ExercicioFinalizado(
                nome = exercicio.nome,
                concluido = exerciciosMarcados.contains(index),
                series = exercicio.series.map { serie ->
                    SerieFinalizada(
                        numero = serie.numero,
                        repeticoes = serie.repeticoes,
                        carga = serie.peso.toDouble()
                    )
                }
            )
        }

        val treinoFinalizado = TreinoFinalizado(
            userId = userId,
            titulo = treinoAtual.titulo,
            data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            exerciciosCompletos = exerciciosConcluidos,
            totalExercicios = totalExercicios,
            exercicios = exerciciosFinalizados
        )

        FirebaseFirestore.getInstance().collection("treinosFinalizados")
            .add(treinoFinalizado)
            .addOnSuccessListener {
                Toast.makeText(this, "Treino salvo no histórico!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("Firestore", "Erro ao salvar treino", e)
            }
    }
}