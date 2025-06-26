package com.example.fitunifor.aluno

import Aula
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitunifor.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AulasAdapterAluno(
    private var aulas: List<Aula>
) : RecyclerView.Adapter<AulasAdapterAluno.AulaAlunoViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usuarioId = auth.currentUser?.uid ?: ""

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

        // Configura a imagem da aula
        val imagemResId = when (aula.imagem.lowercase()) {
            "image_aula_yoga" -> R.drawable.image_aula_yoga
            "image_aula_zumba" -> R.drawable.image_aula_zumba
            else -> R.drawable.image_aula_coletiva
        }

        Glide.with(holder.itemView.context)
            .load(imagemResId)
            .placeholder(R.drawable.image_aula_coletiva) // Imagem padrão enquanto carrega
            .error(R.drawable.image_aula_coletiva) // Imagem padrão se houver erro
            .into(holder.imagemAula)

        // Configura os demais dados da aula
        holder.nomeAula.text = aula.nome
        holder.professor.text = "Prof ${aula.professor}"
        holder.diaSemana.text = "Dia: ${aula.diaSemana}"
        holder.horario.text = "Horário: ${aula.horario}"
        holder.capacidade.text = "${aula.maxAlunos} alunos"
        holder.textIntegrantes.text = "${aula.alunosMatriculados} alunos"

        // Configura o botão de participar
        val temVagaDisponivel = aula.temVagasDisponiveis()
        val jaMatriculado = aula.usuarioJaMatriculado(usuarioId)

        holder.btnParticipar.text = when {
            jaMatriculado -> "Cancelar"
            temVagaDisponivel -> "Participar"
            else -> "Lotado"
        }
        holder.btnParticipar.isEnabled = temVagaDisponivel || jaMatriculado

        holder.btnParticipar.setOnClickListener {
            val aulaRef = db.collection("aulas").document(aula.id)
            holder.btnParticipar.isEnabled = false

            if (jaMatriculado) {
                cancelarAula(aulaRef, aula, position, holder)
            } else {
                participarAula(aulaRef, aula, position, holder)
            }
        }
    }

    private fun participarAula(
        aulaRef: com.google.firebase.firestore.DocumentReference,
        aula: Aula,
        position: Int,
        holder: AulaAlunoViewHolder
    ) {
        db.runTransaction { transaction ->
            val snapshot = transaction.get(aulaRef)
            val currentAlunos = snapshot.getLong("alunosMatriculados")?.toInt() ?: 0
            val maxAlunos = snapshot.getLong("maxAlunos")?.toInt() ?: 0
            val currentList = snapshot.get("alunosMatriculadosList") as? List<String> ?: listOf()

            if (currentAlunos >= maxAlunos) throw Exception("Sem vagas disponíveis")
            if (currentList.contains(usuarioId)) throw Exception("Usuário já matriculado")

            val novaLista = currentList + usuarioId
            transaction.update(aulaRef, mapOf(
                "alunosMatriculados" to currentAlunos + 1,
                "alunosMatriculadosList" to novaLista
            ))

            currentAlunos + 1
        }.addOnSuccessListener { novosAlunos ->
            aula.alunosMatriculados = novosAlunos
            aula.alunosMatriculadosList = aula.alunosMatriculadosList + usuarioId
            notifyItemChanged(position)
            Toast.makeText(holder.itemView.context, "Matrícula realizada com sucesso!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.e("Participar", "Erro ao participar", e)
            Toast.makeText(holder.itemView.context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            holder.btnParticipar.isEnabled = true
        }
    }

    private fun cancelarAula(
        aulaRef: com.google.firebase.firestore.DocumentReference,
        aula: Aula,
        position: Int,
        holder: AulaAlunoViewHolder
    ) {
        db.runTransaction { transaction ->
            val snapshot = transaction.get(aulaRef)
            val currentAlunos = snapshot.getLong("alunosMatriculados")?.toInt() ?: 0
            val currentList = snapshot.get("alunosMatriculadosList") as? List<String> ?: listOf()

            val novaLista = currentList.filter { it != usuarioId }
            val novosAlunos = (currentAlunos - 1).coerceAtLeast(0)

            transaction.update(aulaRef, mapOf(
                "alunosMatriculados" to novosAlunos,
                "alunosMatriculadosList" to novaLista
            ))

            novosAlunos
        }.addOnSuccessListener { novosAlunos ->
            aula.alunosMatriculados = novosAlunos
            aula.alunosMatriculadosList = aula.alunosMatriculadosList.filter { it != usuarioId }
            notifyItemChanged(position)
            Toast.makeText(holder.itemView.context, "Matrícula cancelada com sucesso!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.e("Cancelar", "Erro ao cancelar", e)
            Toast.makeText(holder.itemView.context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            holder.btnParticipar.isEnabled = true
        }
    }

    override fun getItemCount(): Int = aulas.size

    class AulaAlunoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagemAula: ImageView = itemView.findViewById(R.id.imagem_aula_admin)
        val nomeAula: TextView = itemView.findViewById(R.id.text_nome_aula)
        val professor: TextView = itemView.findViewById(R.id.text_professor)
        val diaSemana: TextView = itemView.findViewById(R.id.text_dia)
        val horario: TextView = itemView.findViewById(R.id.text_horario)
        val capacidade: TextView = itemView.findViewById(R.id.text_capacidade)
        val textIntegrantes: TextView = itemView.findViewById(R.id.text_integrantes)
        val btnParticipar: Button = itemView.findViewById(R.id.button_participar_aula)
    }
}