package com.example.fitunifor.aluno

import ExampleDialogFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R


class TreinoActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino)

        findViewById<View>(R.id.icon_arrow_back_meus_treinos).setOnClickListener {
            voltarParaMeusTreinos()
        }

        // Clique nos botões de play
        findViewById<ImageView>(R.id.playButton3).setOnClickListener {
            mostrarPopup()
        }

        findViewById<ImageView>(R.id.playButton4).setOnClickListener {
            mostrarPopup()
        }

        findViewById<ImageView>(R.id.playButton5).setOnClickListener {
            mostrarPopup()
        }

        val buttonIniciarTreino = findViewById<Button>(R.id.button_iniciar_treino)

        buttonIniciarTreino.setOnClickListener {
            navigateToTreinoIniciado()
        }
    }

    private fun mostrarPopup() {
        val dialog = ExampleDialogFragment()
        dialog.show(supportFragmentManager, "video_dialog")
    }

    private fun voltarParaMeusTreinos() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onSupportNavigateUp(): Boolean {
        voltarParaMeusTreinos()
        return true
    }

    private fun navigateToTreinoIniciado() {
        try {
            val intent = Intent(this, TreinoIniciadoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Não foi possível iniciar o treino\n${e.localizedMessage}",
                Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
