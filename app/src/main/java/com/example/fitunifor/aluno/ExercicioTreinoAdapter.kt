package com.example.fitunifor.aluno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Exercicio

class ExercicioTreinoAdapter(
    private val exercicios: List<Exercicio>,
    private val onPlayClick: (Int) -> Unit
) : RecyclerView.Adapter<ExercicioTreinoAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numeroExercicio: TextView = itemView.findViewById(R.id.text_numero_exercicio)
        private val nomeExercicio: TextView = itemView.findViewById(R.id.text_nome_exercicio)
        private val series: TextView = itemView.findViewById(R.id.text_series)
        private val repeticoes: TextView = itemView.findViewById(R.id.text_repeticoes)
        private val carga: TextView = itemView.findViewById(R.id.text_carga)
        private val videoView: VideoView = itemView.findViewById(R.id.video_exercicio)
        val playButton: ImageView = itemView.findViewById(R.id.button_play_video)

        fun bind(exercicio: Exercicio, position: Int) {
            numeroExercicio.text = (position + 1).toString()
            nomeExercicio.text = exercicio.nome
            series.text = "Séries: 4" // Você pode adicionar esses dados ao modelo Exercicio se necessário
            repeticoes.text = "Repetições: 8"
            carga.text = "Carga: 30 kg"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_treino_aluno, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        holder.bind(exercicios[position], position)
        holder.playButton.setOnClickListener { onPlayClick(position) }
    }

    override fun getItemCount(): Int = exercicios.size
}