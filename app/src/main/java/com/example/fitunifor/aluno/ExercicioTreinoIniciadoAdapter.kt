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
    private val onCheckChange: (Int, Boolean) -> Unit,
    private val onPlayClick: (Int) -> Unit  // Mantido como Int para compatibilidade
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

            // Atualiza as séries
            exercicio.series.forEachIndexed { index, serie ->
                when (index) {
                    0 -> serie1.text = "• ${serie.repeticoes} repetições • ${serie.peso}kg"
                    1 -> serie2.text = "• ${serie.repeticoes} repetições • ${serie.peso}kg"
                    2 -> serie3.text = "• ${serie.repeticoes} repetições • ${serie.peso}kg"
                }
            }

            // Visibilidade das séries
            serie1.visibility = if (exercicio.series.size >= 1) View.VISIBLE else View.GONE
            serie2.visibility = if (exercicio.series.size >= 2) View.VISIBLE else View.GONE
            serie3.visibility = if (exercicio.series.size >= 3) View.VISIBLE else View.GONE

            // Checkbox listener
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckChange(position, isChecked)
            }

            // Configura o botão de play
            playButton.setOnClickListener {
                onPlayClick(position)  // Mantém passando a posição
            }

            // Esconde o ícone de play se não houver vídeo
            playButton.visibility = if (exercicio.videoUrl.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
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