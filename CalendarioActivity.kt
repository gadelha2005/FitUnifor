package com.example.fitunifor.aluno


import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fitunifor.R
import java.util.Calendar
import java.util.Locale

class CalendarioActivity : AppCompatActivity() {

    private lateinit var btnTreino: ToggleButton
    private lateinit var btnAula: ToggleButton
    private lateinit var gridDias: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)
        val textDataAtual = findViewById<TextView>(R.id.text_data_atual)
        val calendarioHoje = Calendar.getInstance()
        val formato = java.text.SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        val dataFormatada = formato.format(calendarioHoje.time)
        textDataAtual.text = dataFormatada
        val mesAno = findViewById<TextView>(R.id.mes_ano)
        val formato2 = java.text.SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        val dataFormatada2 = formato2.format(calendarioHoje.time)
        mesAno.text = dataFormatada2.capitalize()

        gridDias = findViewById(R.id.grid_dias)
        gerarDiasDoMes()

        val iconBackPrincipal = findViewById<ImageView>(R.id.icon_back_principal)
        btnTreino = findViewById(R.id.button_treino_diario)
        btnAula = findViewById(R.id.button_aula_diaria)

        iconBackPrincipal.setOnClickListener {
            navigatePrincipal()
        }

        btnTreino.setOnCheckedChangeListener { _, _ ->
            atualizarFiltro()
        }

        btnAula.setOnCheckedChangeListener { _, _ ->
            atualizarFiltro()
        }

        atualizarFiltro()
    }

    private fun navigatePrincipal() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun atualizarFiltro() {
        val corAtivo = Color.parseColor("#252525")
        val corInativo = Color.parseColor("#80252525") // 50% opacidade

        btnTreino.setBackgroundColor(if (btnTreino.isChecked) corAtivo else corInativo)
        btnAula.setBackgroundColor(if (btnAula.isChecked) corAtivo else corInativo)

        btnTreino.setTextColor(Color.WHITE)
        btnAula.setTextColor(Color.WHITE)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // Oculta todos antes
        transaction.replace(R.id.frame_layout_aula, Fragment())
        transaction.replace(R.id.frame_layout_treino, Fragment())

        if (btnTreino.isChecked && !btnAula.isChecked) {
            transaction.replace(R.id.frame_layout_treino, TreinoFragment())
        } else if (btnAula.isChecked && !btnTreino.isChecked) {
            transaction.replace(R.id.frame_layout_aula, AulaFragment())
        } else if (btnTreino.isChecked && btnAula.isChecked) {
            transaction.replace(R.id.frame_layout_treino, TreinoFragment())
            transaction.replace(R.id.frame_layout_aula, AulaFragment())
        }

        transaction.commit()
    }

    private fun gerarDiasDoMes() {
        gridDias.removeAllViews()
        gridDias.columnCount = 7 // garantir 7 colunas

        val calendario = Calendar.getInstance()
        calendario.set(Calendar.DAY_OF_MONTH, 1)

        val primeiroDiaSemana = calendario.get(Calendar.DAY_OF_WEEK) - 1
        val diasNoMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH)
        val hoje = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val totalCelulas = primeiroDiaSemana + diasNoMes

        for (i in 0 until totalCelulas) {
            val textView = TextView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setGravity(Gravity.FILL)
                    setMargins(8, 8, 8, 8)
                }

                gravity = Gravity.CENTER
                setPadding(8, 16, 8, 16)
                textSize = 16f

                if (i >= primeiroDiaSemana) {
                    val dia = i - primeiroDiaSemana + 1
                    text = dia.toString()
                    setTextColor(Color.BLACK)

                    if (dia == hoje) {
                        setBackgroundResource(R.drawable.circle_background)
                        setTextColor(Color.WHITE)
                    }
                } else {
                    text = ""
                }
            }
            gridDias.addView(textView)
        }
    }


}