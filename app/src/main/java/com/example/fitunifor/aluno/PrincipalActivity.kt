package com.example.fitunifor.aluno

import Aula
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar

class PrincipalActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var recyclerAulas: RecyclerView
    private lateinit var adapter: AulasAdapterAluno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        setupNomeAluno()

        recyclerAulas = findViewById(R.id.recycler_aulas_diarias)
        setupAulasRecyclerView()

        // Configuração de botões e cards
        findViewById<CardView>(R.id.card_meus_treinos).setOnClickListener { navigateToMeusTreinos() }
        findViewById<CardView>(R.id.card_calendario).setOnClickListener { navigateCalendario() }
        findViewById<CardView>(R.id.card_ia).setOnClickListener { navigateIa() }
        findViewById<Button>(R.id.button_iniciar_treino1).setOnClickListener { navigateToTreinoIniciado() }

        carregarAulasDoFirebase()
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
        val ref = db.collection("aulas")
        val diaAtual = getDiaSemanaAtual()

        ref.whereEqualTo("diaSemana", diaAtual)
            .get()
            .addOnSuccessListener { result ->
                val listaAulas = mutableListOf<Aula>()
                for (document in result) {
                    val aula = document.toObject(Aula::class.java)
                    listaAulas.add(aula)
                }
                adapter.atualizarAulas(listaAulas)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar aulas: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun getDiaSemanaAtual(): String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("EEEE", java.util.Locale("pt", "BR")).format(calendar.time)
            .replaceFirstChar { it.uppercase() }
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

    private fun showErrorToast(message: String, exception: Exception) {
        Toast.makeText(this, "$message\n${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        exception.printStackTrace()
    }
}
