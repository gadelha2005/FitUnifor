package com.example.fitunifor.aluno

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fitunifor.R

class CalendarioActivity : AppCompatActivity() {

    private lateinit var btnTreino: ToggleButton
    private lateinit var btnAula: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

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



}
