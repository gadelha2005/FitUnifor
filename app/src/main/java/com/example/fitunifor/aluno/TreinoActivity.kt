package com.example.fitunifor.aluno

import ExampleDialogFragment
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

        // Configura o topo da tela
        findViewById<ImageView>(R.id.icon_arrow_back_meus_treinos).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val tituloTreino = findViewById<TextView>(R.id.text_titulo_treino)
        tituloTreino.text = treino?.titulo ?: "Treino"

        // Configura o RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_exercicios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val exercicios = treino?.exercicios ?: emptyList()
        val adapter = ExercicioTreinoAdapter(exercicios) { position ->
            mostrarPopup()
        }
        recyclerView.adapter = adapter

        // Configura o botão de iniciar treino
        val buttonIniciarTreino = findViewById<Button>(R.id.button_iniciar_treino)
        buttonIniciarTreino.setOnClickListener {
            navigateToTreinoIniciado(treino)
        }
    }

    private fun mostrarPopup() {
        val dialog = ExampleDialogFragment()
        dialog.show(supportFragmentManager, "video_dialog")
    }

    private fun navigateToTreinoIniciado(treino: Treino?) {
        try {
            val intent = Intent(this, TreinoIniciadoActivity::class.java).apply {
                putExtra("TREINO", treino)
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