package com.example.fitunifor.administrador.aulas

import Aula
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.bumptech.glide.Glide // Adicione esta linha

class AulaAdapterAdmin(
    private val listaAulas: MutableList<Aula>,
    private val onEditarClick: (Aula) -> Unit,
    private val onRemoverClick: (String) -> Unit
) : RecyclerView.Adapter<AulaAdapterAdmin.AulaViewHolder>() {

    inner class AulaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagemAula: ImageView = itemView.findViewById(R.id.imagem_aula_admin)
        val nomeAula: TextView = itemView.findViewById(R.id.text_nome_aula)
        val professor: TextView = itemView.findViewById(R.id.text_professor)
        val dia: TextView = itemView.findViewById(R.id.text_dia)
        val horario: TextView = itemView.findViewById(R.id.text_horario)
        val capacidade: TextView = itemView.findViewById(R.id.text_capacidade)
        val integrantes: TextView = itemView.findViewById(R.id.text_integrantes)
        val btnEditar: Button = itemView.findViewById(R.id.button_editar_aula)
        val btnRemover: Button = itemView.findViewById(R.id.button_remover_aula)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aula_admin, parent, false)
        return AulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        val aula = listaAulas[position]

        // Mapeando o nome da imagem para o recurso correspondente
        val imagemResId = when (aula.imagem) {
            "image_aula_yoga" -> R.drawable.image_aula_yoga
            "image_aula_zumba" -> R.drawable.image_aula_zumba
            else -> R.drawable.image_aula_coletiva // Imagem padrão para aulas genéricas
        }

        // Carrega a imagem com Glide, utilizando o resource ID
        Glide.with(holder.itemView.context)
            .load(imagemResId) // Carrega a imagem com o ID do recurso
            .placeholder(R.drawable.image_aula_coletiva) // Imagem padrão
            .into(holder.imagemAula)

        holder.nomeAula.text = aula.nome
        holder.professor.text = "Prof. ${aula.professor}"
        holder.dia.text = "Dia: ${aula.diaSemana}"
        holder.horario.text = "Horário: ${aula.horario}"
        holder.capacidade.text = "${aula.maxAlunos} alunos"
        holder.integrantes.text = "${aula.alunosMatriculados} alunos"

        holder.btnEditar.setOnClickListener { onEditarClick(aula) }
        holder.btnRemover.setOnClickListener { onRemoverClick(aula.id) }
    }

    override fun getItemCount() = listaAulas.size

    fun atualizarLista(novasAulas: List<Aula>) {
        listaAulas.clear()
        listaAulas.addAll(novasAulas)
        notifyDataSetChanged()
    }
}
