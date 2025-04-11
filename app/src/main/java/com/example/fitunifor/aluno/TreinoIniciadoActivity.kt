package com.example.fitunifor.aluno

import ExampleDialogFragment
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R

class TreinoIniciadoActivity : AppCompatActivity() {

    private lateinit var iconVoltarTelaInicial: ImageView
    private lateinit var timerText: TextView
    private lateinit var startButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var resetButton: ImageView
    private lateinit var editTimerIcon: ImageView
    private lateinit var vibrator: Vibrator
    private lateinit var textExerciciosFeitos: TextView

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis: Long = 60000
    private var initialTimeInMillis: Long = 60000
    private var totalExercicios = 3
    private var exerciciosConcluidos = 0

    @SuppressLint("MissingInflatedId", "ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino_iniciado)

        // Inicializa o vibrator
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Inicializa os componentes da interface
        iconVoltarTelaInicial = findViewById(R.id.icon_back_historico_treinos)
        timerText = findViewById(R.id.timer_text)
        startButton = findViewById(R.id.icon_start_timer)
        pauseButton = findViewById(R.id.icon_pause_timer)
        resetButton = findViewById(R.id.icon_reset_timer)
        editTimerIcon = findViewById(R.id.icon_edit_timer)
        textExerciciosFeitos = findViewById(R.id.text_exercicios_feitos)

        // Estado inicial
        updateTimerDisplay()
        updateExerciciosFeitosText()
        showStartButton()

        // Configura listeners
        setupClickListeners()
        setupCheckBoxListeners()
        setupVideoIconListener()
    }

    private fun setupVideoIconListener() {
        val videoIcon = findViewById<ImageView>(R.id.icon_play_video_supino_inclinado)
        videoIcon.setOnClickListener {
            showVideoDialog()
        }
    }

    private fun showVideoDialog() {
        val dialog = ExampleDialogFragment()
        dialog.show(supportFragmentManager, "ExampleDialogFragment")
    }

    private fun setupClickListeners() {

        iconVoltarTelaInicial.setOnClickListener { navigateTelaPrincipal() }

        // Botão de iniciar o timer
        startButton.setOnClickListener {
            startTimer()
            showPauseButton()
        }

        // Botão de pausar o timer
        pauseButton.setOnClickListener {
            pauseTimer()
            showStartButton()
        }

        // Botão de resetar o timer
        resetButton.setOnClickListener {
            resetTimer()
            showStartButton()
        }

        editTimerIcon.setOnClickListener {
            countDownTimer?.cancel()
            timerRunning = false

            val originalTimeInSeconds = (initialTimeInMillis / 1000).toInt()
            val dialog = TimerDialogFragment(originalTimeInSeconds) { selectedTimeInSeconds ->
                initialTimeInMillis = selectedTimeInSeconds * 1000L
                timeLeftInMillis = initialTimeInMillis

                updateTimerDisplay()
                showStartButton()
                Toast.makeText(this, "Tempo atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            dialog.show(supportFragmentManager, "TimerDialog")
        }
    }

    private fun setupCheckBoxListeners() {
        val checkBoxExercicio1 = findViewById<CheckBox>(R.id.checkBoxExercicio1)
        val checkBoxExercicio2 = findViewById<CheckBox>(R.id.checkBoxExercicio2)
        val checkBoxExercicio3 = findViewById<CheckBox>(R.id.checkBoxExercicio3)

        checkBoxExercicio1.setOnCheckedChangeListener { _, isChecked ->
            updateExerciciosConcluidos(isChecked)
        }

        checkBoxExercicio2.setOnCheckedChangeListener { _, isChecked ->
            updateExerciciosConcluidos(isChecked)
        }

        checkBoxExercicio3.setOnCheckedChangeListener { _, isChecked ->
            updateExerciciosConcluidos(isChecked)
        }
    }

    private fun updateExerciciosConcluidos(isChecked: Boolean) {
        exerciciosConcluidos += if (isChecked) 1 else -1
        updateExerciciosFeitosText()
    }

    private fun updateExerciciosFeitosText() {
        textExerciciosFeitos.text = "$exerciciosConcluidos de $totalExercicios exercícios"
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                timerRunning = false
                showStartButton()
                vibrate()
                resetTimer()
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

    private fun navigateTelaPrincipal() {
        try {
            val intent = Intent(this, PrincipalActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Não foi possível abrir a Tela Principal\n${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        vibrator.cancel()
    }
}