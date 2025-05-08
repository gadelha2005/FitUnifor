package com.example.fitunifor.aluno

import Aula
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.google.firebase.firestore.FirebaseFirestore

class AulasAdapterAluno(
    private var aulas: List<Aula>
) : RecyclerView.Adapter<AulasAdapterAluno.AulaAlunoViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    fun atualizarAulas(novasAulas: List<Aula>) {
        aulas = novasAulas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaAlunoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aula_aluno, parent, false)
        return AulaAlunoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaAlunoViewHolder, position: Int) {
        val aula = aulas[position]
        Log.d("AulasAdapter", "Binding aula: ${aula.nome} - Matriculados: ${aula.alunosMatriculados}")

        // Atualiza a UI com os valores atuais
        holder.nomeAula.text = aula.nome
        holder.professor.text = "Prof ${aula.professor}"
        holder.horario.text = "Horário: ${aula.horario}"
        holder.capacidade.text = "${aula.maxAlunos} alunos"
        holder.textIntegrantes.text = "${aula.alunosMatriculados} alunos"

        val temVagaDisponivel = aula.alunosMatriculados < aula.maxAlunos
        holder.btnParticipar.text = when {
            aula.alunosMatriculados > 0 -> "Cancelar"
            temVagaDisponivel -> "Participar"
            else -> "Lotado"
        }
        holder.btnParticipar.isEnabled = temVagaDisponivel || aula.alunosMatriculados > 0

        holder.btnParticipar.setOnClickListener {
            val aulaRef = db.collection("aulas").document(aula.id)
            holder.btnParticipar.isEnabled = false

            if (holder.btnParticipar.text == "Participar") {
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(aulaRef)
                    val currentAlunos = snapshot.getLong("alunosMatriculados")?.toInt() ?: 0
                    val maxAlunos = snapshot.getLong("maxAlunos")?.toInt() ?: 0

                    if (currentAlunos >= maxAlunos) {
                        throw Exception("Sem vagas disponíveis")
                    }

                    transaction.update(aulaRef, "alunosMatriculados", currentAlunos + 1)
                    currentAlunos + 1 // Retorna novo valor para o successListener
                }.addOnSuccessListener { novosAlunos ->
                    aula.alunosMatriculados = novosAlunos
                    notifyItemChanged(position)
                }.addOnFailureListener { e ->
                    Log.e("Participar", "Erro ao participar", e)
                    Toast.makeText(holder.itemView.context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                    holder.btnParticipar.isEnabled = true
                }
            } else {
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(aulaRef)
                    val currentAlunos = snapshot.getLong("alunosMatriculados")?.toInt() ?: 0
                    val novosAlunos = (currentAlunos - 1).coerceAtLeast(0)
                    transaction.update(aulaRef, "alunosMatriculados", novosAlunos)
                    novosAlunos // Retorna novo valor para o successListener
                }.addOnSuccessListener { novosAlunos ->
                    aula.alunosMatriculados = novosAlunos
                    notifyItemChanged(position)
                }.addOnFailureListener { e ->
                    Log.e("Cancelar", "Erro ao cancelar", e)
                    Toast.makeText(holder.itemView.context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                    holder.btnParticipar.isEnabled = true
                }
            }
        }
    }

    override fun getItemCount(): Int = aulas.size

    class AulaAlunoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeAula: TextView = itemView.findViewById(R.id.text_nome_aula)
        val professor: TextView = itemView.findViewById(R.id.text_professor)
        val horario: TextView = itemView.findViewById(R.id.text_horario)
        val capacidade: TextView = itemView.findViewById(R.id.text_capacidade)
        val textIntegrantes: TextView = itemView.findViewById(R.id.text_integrantes)
        val btnParticipar: Button = itemView.findViewById(R.id.button_participar_aula)
    }
}