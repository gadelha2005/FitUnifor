package com.example.fitunifor.aluno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.model.TreinoFinalizado

class TreinoHistoricoAdapter (
    private val treinosFinalizados: List<TreinoFinalizado>,
    private val onItemClick: (TreinoFinalizado) -> Unit
) : RecyclerView.Adapter<TreinoHistoricoAdapter.HistoricoViewHolder>() {

    inner class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloTreino: TextView = itemView.findViewById(R.id.text_titulo_treino)
        private val dataTreino: TextView = itemView.findViewById(R.id.text_data_treino)
        private val desempenho: TextView = itemView.findViewById(R.id.text_desempenho)
        private val cardView: CardView = itemView.findViewById(R.id.card_treino_finalizado)

        fun bind(treino: TreinoFinalizado) {
            tituloTreino.text = treino.titulo
            dataTreino.text = treino.data
            desempenho.text = "Desempenho: ${treino.exerciciosCompletos} de ${treino.totalExercicios} exercícios"

            cardView.setOnClickListener { onItemClick(treino) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treino_historico_treinos, parent, false)
        return HistoricoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        holder.bind(treinosFinalizados[position])
    }

    override fun getItemCount(): Int = treinosFinalizados.size
}