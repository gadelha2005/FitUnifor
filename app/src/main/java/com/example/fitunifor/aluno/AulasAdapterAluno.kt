package com.example.fitunifor.aluno

import Aula
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Button
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class AulasAdapterAluno(
    private val aulas: List<Aula>,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<AulasAdapterAluno.AulaAlunoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaAlunoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aula_aluno, parent, false)
        return AulaAlunoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaAlunoViewHolder, position: Int) {
        val aula = aulas[position]

        holder.nomeAula.text = aula.nome
        holder.professor.text = "Prof ${aula.professor}"
        holder.horario.text = aula.horario
        holder.capacidade.text = "${aula.maxAlunos}/${aula.alunosMatriculados}"

        // Mostra o número de participantes
        holder.textIntegrantes.text = "${aula.alunosMatriculados} alunos"

        // Configura o botão de participação
        if (aula.alunosMatriculados < aula.maxAlunos) {
            if (aula.alunosMatriculados > 0) {
                holder.btnParticipar.text = "Cancelar"
            } else {
                holder.btnParticipar.text = "Participar"
            }
        } else {
            holder.btnParticipar.text = "Sem vagas"
            holder.btnParticipar.isEnabled = false
        }

        holder.btnParticipar.setOnClickListener {
            // Verifica se o aluno já está inscrito
            if (aula.alunosMatriculados < aula.maxAlunos && holder.btnParticipar.text == "Participar") {
                // O aluno não está participando e há vagas disponíveis
                aula.alunosMatriculados++
                holder.btnParticipar.text = "Cancelar"
            } else if (aula.alunosMatriculados > 0 && holder.btnParticipar.text == "Cancelar") {
                // O aluno está participando, então ele pode cancelar
                aula.alunosMatriculados--
                holder.btnParticipar.text = "Participar"
            } else {
                // Se não houver vagas ou o aluno não pode cancelar, mostra um aviso
                Toast.makeText(holder.itemView.context, "Sem vagas disponíveis ou você não está inscrito", Toast.LENGTH_SHORT).show()
            }

            // Atualiza o número de participantes
            holder.textIntegrantes.text = "${aula.alunosMatriculados} alunos"
            notifyItemChanged(position)
        }

    }

    override fun getItemCount(): Int {
        return aulas.size
    }

    class AulaAlunoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeAula: TextView = itemView.findViewById(R.id.text_nome_aula)
        val professor: TextView = itemView.findViewById(R.id.text_professor)
        val horario: TextView = itemView.findViewById(R.id.text_horario)
        val capacidade: TextView = itemView.findViewById(R.id.text_capacidade)
        val textIntegrantes: TextView = itemView.findViewById(R.id.text_integrantes)
        val btnParticipar: Button = itemView.findViewById(R.id.button_participar_aula)
    }
}
