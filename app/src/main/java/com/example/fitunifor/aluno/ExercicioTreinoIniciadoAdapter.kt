package com.example.fitunifor.aluno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Exercicio

class ExercicioTreinoIniciadoAdapter(
    private val exercicios: List<Exercicio>,
    private val onCheckChange: (Boolean) -> Unit,
    private val onPlayClick: (Int) -> Unit
) : RecyclerView.Adapter<ExercicioTreinoIniciadoAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeExercicio: TextView = itemView.findViewById(R.id.text_nome_exercicio)
        private val serie1: TextView = itemView.findViewById(R.id.text_serie1)
        private val serie2: TextView = itemView.findViewById(R.id.text_serie2)
        private val serie3: TextView = itemView.findViewById(R.id.text_serie3)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxExercicio)
        val playButton: ImageView = itemView.findViewById(R.id.icon_play_video)

        fun bind(exercicio: Exercicio, position: Int) {
            nomeExercicio.text = exercicio.nome
            serie1.text = "• 1. 8 repetições • 30kg"
            serie2.text = "• 2. 8 repetições • 30kg"
            serie3.text = "• 3. 8 repetições • 30kg"

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckChange(isChecked)
            }

            playButton.setOnClickListener {
                onPlayClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_treino_iniciado, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        holder.bind(exercicios[position], position)
    }

    override fun getItemCount(): Int = exercicios.size
}