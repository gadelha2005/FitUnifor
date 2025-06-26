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
    private val onPlayClick: (Exercicio) -> Unit
) : RecyclerView.Adapter<ExercicioTreinoAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numero: TextView = itemView.findViewById(R.id.text_numero_exercicio)
        private val nome: TextView = itemView.findViewById(R.id.text_nome_exercicio)
        private val series: TextView = itemView.findViewById(R.id.text_series)
        private val repeticoes: TextView = itemView.findViewById(R.id.text_repeticoes)
        private val carga: TextView = itemView.findViewById(R.id.text_carga)
        private val playButton: ImageView = itemView.findViewById(R.id.button_play_video)

        fun bind(exercicio: Exercicio, position: Int) {
            numero.text = (position + 1).toString()
            nome.text = exercicio.nome
            exercicio.series.firstOrNull()?.let { s ->
                series.text = "Séries: ${exercicio.series.size}"
                repeticoes.text = "Repetições: ${s.repeticoes}"
                carga.text = "Carga: ${s.peso} kg"
            }

            playButton.setOnClickListener { onPlayClick(exercicio) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExercicioViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_exercicio_treino_aluno, parent, false)
        )

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) =
        holder.bind(exercicios[position], position)

    override fun getItemCount() = exercicios.size
}
