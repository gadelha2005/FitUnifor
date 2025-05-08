package com.example.fitunifor.administrador.fichas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class FichasTreinoFragment : Fragment(R.layout.fragment_fichas_treino) {

    private lateinit var adapter: AlunoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaAlunos = listOf(
            Aluno("1", "João Silva", "joao@gmail.com"),
            Aluno("2", "Maria Souza", "maria@gmail.com"),
            Aluno("3", "Carlos Oliveira", "carlos@gmail.com")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_alunos)
        val editTextBusca = view.findViewById<EditText>(R.id.edit_text_professor)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AlunoAdapter(listaAlunos) { aluno ->
            val intent = Intent(activity, GestaoTreinosActivity::class.java).apply {
                putExtra("aluno_id", aluno.id)
                putExtra("aluno_nome", aluno.nome)
                putExtra("aluno_email", aluno.email)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        editTextBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}