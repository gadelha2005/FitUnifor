package com.example.fitunifor.aluno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.model.TreinoFinalizado

class TreinoHistoricoAdapter(
    private var treinos: List<TreinoFinalizado>,
    private val onItemClick: (TreinoFinalizado) -> Unit
) : RecyclerView.Adapter<TreinoHistoricoAdapter.TreinoViewHolder>() {

    // Método para atualizar a lista
    fun updateList(newList: List<TreinoFinalizado>) {
        treinos = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreinoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treino_historico_treinos, parent, false)
        return TreinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreinoViewHolder, position: Int) {
        holder.bind(treinos[position])
    }

    override fun getItemCount(): Int = treinos.size

    inner class TreinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titulo: TextView = itemView.findViewById(R.id.text_titulo_treino)
        private val data: TextView = itemView.findViewById(R.id.text_data_treino)
        private val desempenho: TextView = itemView.findViewById(R.id.text_desempenho)
        private val imageTreino: ImageView = itemView.findViewById(R.id.image_treino)

        fun bind(treino: TreinoFinalizado) {
            titulo.text = treino.titulo
            data.text = treino.data
            desempenho.text = "${treino.exerciciosCompletos}/${treino.totalExercicios} exercícios concluídos"

            // Definir a imagem com base no tipo de treino
            imageTreino.setImageResource(treino.determinarImagemTreino())
            imageTreino.scaleType = ImageView.ScaleType.CENTER_CROP

            itemView.setOnClickListener {
                onItemClick(treino)
            }
        }
    }
}