package com.example.fitunifor.administrador.fichas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FichasTreinoFragment : Fragment(R.layout.fragment_fichas_treino) {

    private lateinit var adapter: AlunoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextBusca: EditText
    private lateinit var textViewTitulo: TextView
    private val db = Firebase.firestore
    private var todosAlunos: List<Aluno> = emptyList()
    private var searchJob: Job? = null
    private val searchDelay = 500L // Delay para evitar buscas a cada tecla pressionada

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view_alunos)
        editTextBusca = view.findViewById(R.id.edit_text_professor)
        textViewTitulo = view.findViewById(R.id.textView27)

        setupRecyclerView()
        setupSearch()
        carregarAlunosDoFirebase()
    }

    private fun setupRecyclerView() {
        adapter = AlunoAdapter(emptyList()) { aluno ->
            val intent = Intent(activity, GestaoTreinosActivity::class.java).apply {
                putExtra("aluno_id", aluno.id)
                putExtra("aluno_nome", aluno.nome)
                putExtra("aluno_email", aluno.email)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        editTextBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                searchJob?.cancel() // Cancela a busca anterior se ainda estiver pendente

                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(searchDelay) // Aguarda o tempo definido antes de executar a busca

                    if (query.isEmpty()) {
                        // Se a busca estiver vazia, mostra todos os alunos
                        adapter.atualizarLista(todosAlunos)
                        textViewTitulo.text = "Alunos (${todosAlunos.size})"
                    } else {
                        // Filtra localmente a lista de alunos já carregada
                        val alunosFiltrados = todosAlunos.filter { aluno ->
                            aluno.nome.contains(query, ignoreCase = true) ||
                                    aluno.email.contains(query, ignoreCase = true)
                        }

                        withContext(Dispatchers.Main) {
                            if (alunosFiltrados.isEmpty()) {
                                textViewTitulo.text = "Nenhum aluno encontrado para '$query'"
                            } else {
                                textViewTitulo.text = "Alunos encontrados (${alunosFiltrados.size})"
                            }
                            adapter.atualizarLista(alunosFiltrados)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun carregarAlunosDoFirebase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = db.collection("usuarios")
                    .whereEqualTo("tipo", "aluno")
                    .get()
                    .await()

                todosAlunos = querySnapshot.documents.map { document ->
                    Aluno(
                        id = document.id,
                        nome = document.getString("nome") ?: "",
                        email = document.getString("email") ?: ""
                    )
                }

                withContext(Dispatchers.Main) {
                    if (todosAlunos.isEmpty()) {
                        textViewTitulo.text = "Nenhum aluno encontrado"
                    } else {
                        textViewTitulo.text = "Alunos (${todosAlunos.size})"
                        adapter.atualizarLista(todosAlunos)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar alunos: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    textViewTitulo.text = "Erro ao carregar alunos"
                }
            }
        }
    }
}