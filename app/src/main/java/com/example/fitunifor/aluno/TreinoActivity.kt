package com.example.fitunifor.aluno

import ExampleDialogFragment
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitunifor.R


class TreinoActivity : AppCompatActivity() {
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
}
