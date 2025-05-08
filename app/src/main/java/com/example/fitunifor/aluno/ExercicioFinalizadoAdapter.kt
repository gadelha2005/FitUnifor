package com.example.fitunifor.aluno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class ExercicioFinalizadoAdapter(
    private val exercicios: List<ExercicioFinalizado>
) : RecyclerView.Adapter<ExercicioFinalizadoAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeExercicio: TextView = itemView.findViewById(R.id.text_nome_exercicio)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxExercicio)
        private val serie1: TextView = itemView.findViewById(R.id.text_serie1)
        private val serie2: TextView = itemView.findViewById(R.id.text_serie2)
        private val serie3: TextView = itemView.findViewById(R.id.text_serie3)

        fun bind(exercicio: ExercicioFinalizado) {
            nomeExercicio.text = exercicio.nome
            checkBox.isChecked = exercicio.concluido

            // Mostra as séries (assumindo 3 séries)
            exercicio.series.take(3).forEachIndexed { index, serie ->
                when (index) {
                    0 -> serie1.text = "• ${index + 1}. ${serie.repeticoes} repetições • ${serie.peso}kg"
                    1 -> serie2.text = "• ${index + 1}. ${serie.repeticoes} repetições • ${serie.peso}kg"
                    2 -> serie3.text = "• ${index + 1}. ${serie.repeticoes} repetições • ${serie.peso}kg"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_finalizado, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        holder.bind(exercicios[position])
    }

    override fun getItemCount(): Int = exercicios.size
}