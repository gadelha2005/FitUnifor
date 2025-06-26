package com.example.fitunifor.aluno

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Treino

class TreinoActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino_aluno)

        val treino = intent.getParcelableExtra<Treino>("TREINO")

        findViewById<ImageView>(R.id.icon_arrow_back_meus_treinos).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        findViewById<TextView>(R.id.text_titulo_treino).text = treino?.titulo ?: "Treino"

        val exercicios = treino?.exercicios ?: emptyList()

        val recycler = findViewById<RecyclerView>(R.id.recycler_exercicios).apply {
            layoutManager = LinearLayoutManager(this@TreinoActivity)
            adapter = ExercicioTreinoAdapter(exercicios) { ex ->
                ex.videoUrl?.let { showVideoDialog(ex.nome, it) }
                    ?: Toast.makeText(
                        this@TreinoActivity,
                        "Vídeo não disponível para este exercício",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

        findViewById<Button>(R.id.button_iniciar_treino).setOnClickListener {
            navigateToTreinoIniciado(treino)
        }
    }

    private fun showVideoDialog(titulo: String, fileName: String) =
        ExampleDialogFragment.newInstance(titulo, fileName)
            .show(supportFragmentManager, "VideoDialog")

    private fun navigateToTreinoIniciado(treino: Treino?) {
        try {
            val intent = Intent(this, TreinoIniciadoActivity::class.java).apply {
                putExtra("TREINO", treino)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Não foi possível iniciar o treino\n${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
