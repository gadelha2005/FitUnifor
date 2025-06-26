package com.example.fitunifor.administrador.fichas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class AlunoAdapter(
    private var alunos: List<Aluno>,
    private val onItemClick: (Aluno) -> Unit
) : RecyclerView.Adapter<AlunoAdapter.AlunoViewHolder>() {

    inner class AlunoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_aluno)
        val textNome: TextView = itemView.findViewById(R.id.text_nome_aluno)
        val textEmail: TextView = itemView.findViewById(R.id.text_email_aluno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlunoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aluno, parent, false)
        return AlunoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlunoViewHolder, position: Int) {
        val aluno = alunos[position]
        holder.textNome.text = aluno.nome
        holder.textEmail.text = aluno.email
        holder.cardView.setOnClickListener { onItemClick(aluno) }
    }

    override fun getItemCount(): Int = alunos.size

    fun atualizarLista(novaLista: List<Aluno>) {
        alunos = novaLista
        notifyDataSetChanged()
    }

    fun filter(text: String) {
        val listaFiltrada = if (text.isEmpty()) {
            alunos
        } else {
            alunos.filter { aluno ->
                aluno.nome.contains(text, ignoreCase = true) ||
                        aluno.email.contains(text, ignoreCase = true)
            }
        }
        atualizarLista(listaFiltrada)
    }
}