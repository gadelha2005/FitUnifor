package com.example.fitunifor.aluno

import Aula
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.MainActivity
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Treino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrincipalActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var recyclerAulas: RecyclerView
    private lateinit var adapter: AulasAdapterAluno
    private lateinit var textTreinosCompletos: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setupNomeAluno()
        setupViews()
        setupAulasRecyclerView()
        setupClickListeners()

        verificarEResetarInscricoes() // Nova função adicionada
        carregarAulasDoFirebase()
        carregarTreinosCompletos()
    }

    private fun verificarEResetarInscricoes() {
        val ultimoReset = sharedPref.getLong("ultimo_reset", 0)
        val hoje = Calendar.getInstance()

        // Verifica se é segunda-feira e se já passou da hora de reset
        if (hoje.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
            hoje.timeInMillis - ultimoReset > 24 * 60 * 60 * 1000) {

            resetarInscricoesNoFirestore()

            // Atualiza o último horário de reset
            sharedPref.edit().putLong("ultimo_reset", hoje.timeInMillis).apply()
        }
    }

    private fun resetarInscricoesNoFirestore() {
        db.collection("aulas")
            .get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()

                for (document in documents) {
                    val aulaRef = db.collection("aulas").document(document.id)
                    batch.update(aulaRef,
                        "alunosMatriculados", 0,
                        "listaAlunos", emptyList<String>()
                    )
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d("PrincipalActivity", "Inscrições resetadas com sucesso")
                        // Recarrega as aulas após o reset
                        carregarAulasDoFirebase()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PrincipalActivity", "Erro ao resetar inscrições", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PrincipalActivity", "Erro ao buscar aulas para reset", e)
            }
    }

    private fun setupViews() {
        recyclerAulas = findViewById(R.id.recycler_aulas_diarias)
        textTreinosCompletos = findViewById(R.id.text_treinos_completos)
    }

    private fun setupClickListeners() {
        findViewById<CardView>(R.id.card_meus_treinos).setOnClickListener { navigateToMeusTreinos() }
        findViewById<CardView>(R.id.card_calendario).setOnClickListener { navigateCalendario() }
        findViewById<CardView>(R.id.card_ia).setOnClickListener { navigateIa() }
        findViewById<Button>(R.id.button_iniciar_treino1).setOnClickListener { navigateToTreinoIniciado() }

        val logoutIcon = findViewById<ImageView>(R.id.icon_log_out)
        logoutIcon.setOnClickListener { exibirDialogLogout() }
    }

    private fun carregarTreinosCompletos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("treinosFinalizados")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val count = documents.size()
                textTreinosCompletos.text = count.toString()
                Log.d("PrincipalActivity", "Treinos completos: $count")
            }
            .addOnFailureListener { exception ->
                Log.e("PrincipalActivity", "Erro ao carregar treinos completos", exception)
                textTreinosCompletos.text = "0"
            }
    }

    private fun exibirDialogLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sair da conta")
            .setMessage("Deseja realmente sair?")
            .setPositiveButton("Sim") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
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
        val diaAtual = getDiaSemanaAtual()
        Log.d("PrincipalActivity", "Carregando aulas para: $diaAtual")

        db.collection("aulas")
            .whereEqualTo("diaSemana", diaAtual)
            .get()
            .addOnSuccessListener { result ->
                val listaAulas = mutableListOf<Aula>()
                for (document in result) {
                    val aula = document.toObject(Aula::class.java).apply {
                        id = document.id
                        alunosMatriculados = document.getLong("alunosMatriculados")?.toInt() ?: 0
                        Log.d("PrincipalActivity", "Aula: $nome - Dia: $diaSemana - Vagas: $alunosMatriculados/$maxAlunos")
                    }
                    listaAulas.add(aula)
                }
                adapter.atualizarAulas(listaAulas)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar aulas: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("PrincipalActivity", "Erro ao carregar aulas", exception)
            }
    }

    private fun getDiaSemanaAtual(): String {
        val calendar = Calendar.getInstance()
        val diaFormatado = SimpleDateFormat("EEEE", Locale("pt", "BR")).format(calendar.time)
            .replaceFirstChar { it.uppercase() }
        Log.d("PrincipalActivity", "Dia atual: $diaFormatado")
        return diaFormatado
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