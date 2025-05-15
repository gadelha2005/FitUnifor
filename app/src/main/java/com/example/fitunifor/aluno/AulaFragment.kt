package com.example.fitunifor.aluno

import Aula
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AulaFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AulasAdapterAluno
    private val listaAulas = mutableListOf<Aula>()
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_aula_aluno, container, false)

        recyclerView = view.findViewById(R.id.recycler_aulas_diarias)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AulasAdapterAluno(
            listaAulas  // Removido o 'b'
        )
        recyclerView.adapter = adapter

        carregarAulasDoDia()

        return view
    }

    private fun carregarAulasDoDia() {
        val diaAtual = getDiaSemanaAtual()
        Log.d("AulaFragment", "Buscando aulas para: $diaAtual")

        db.collection("aulas")
            .whereEqualTo("diaSemana", diaAtual)
            .get()
            .addOnSuccessListener { result ->
                listaAulas.clear()
                for (document in result) {
                    val aula = document.toObject(Aula::class.java).apply {
                        id = document.id
                        alunosMatriculados = document.getLong("alunosMatriculados")?.toInt() ?: 0
                        alunosMatriculadosList = document.get("alunosMatriculadosList") as List<String> // Carrega a lista de matriculados
                        Log.d("AulaFragment", "Aula carregada: $nome - Dia: $diaSemana - Vagas: $alunosMatriculados/$maxAlunos")
                    }
                    listaAulas.add(aula)
                }
                adapter.atualizarAulas(listaAulas)
            }
            .addOnFailureListener { exception ->
                Log.e("AulaFragment", "Erro ao carregar aulas", exception)
                Toast.makeText(context, "Erro ao carregar aulas: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun getDiaSemanaAtual(): String {
        val calendar = Calendar.getInstance()
        val diaFormatado = SimpleDateFormat("EEEE", Locale("pt", "BR")).format(calendar.time)
            .replaceFirstChar { it.uppercase() }
        Log.d("DiaSemana", "Dia formatado: $diaFormatado")
        return diaFormatado
    }
}
