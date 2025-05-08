package com.example.fitunifor.aluno

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R

class IAActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_ia)

        val iconVoltarPrincipal = findViewById<ImageView>(R.id.icon_voltar_principal)

        iconVoltarPrincipal.setOnClickListener {
            navigateBackToPrincipal()
        }
    }

    private fun navigateBackToPrincipal(){
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}