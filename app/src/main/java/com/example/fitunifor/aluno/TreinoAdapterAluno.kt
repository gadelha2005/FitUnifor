package com.example.fitunifor.aluno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Treino

class TreinoAdapterAluno(
    private val treinos: List<Treino>,
    private val onItemClick: (Treino) -> Unit,
    private val onButtonClick: (Treino) -> Unit
) : RecyclerView.Adapter<TreinoAdapterAluno.TreinoViewHolder>() {

    inner class TreinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloTreino: TextView = itemView.findViewById(R.id.text_titulo_treino)
        private val numeroExercicios: TextView = itemView.findViewById(R.id.text_numero_exercicios)
        private val diaDaSemana: TextView = itemView.findViewById(R.id.text_dia_semana)
        private val buttonIniciar: Button = itemView.findViewById(R.id.button_iniciar_treino)
        private val cardView: CardView = itemView.findViewById(R.id.card_treino)
        private val imageTreino: ImageView = itemView.findViewById(R.id.image_treino) // ID correto

        fun bind(treino: Treino) {
            tituloTreino.text = treino.titulo
            numeroExercicios.text = "${treino.exercicios?.size ?: 0} exercícios"
            diaDaSemana.text = treino.diaDaSemana

            // Definir a imagem com base no tipo de treino
            val imageResource = when (treino.determinarImagemTreino()) {
                "treino_peito" -> R.drawable.treino_peito
                "treino_costas" -> R.drawable.treino_costas
                "treino_perna" -> R.drawable.treino_perna
                else -> R.drawable.treino
            }
            imageTreino.setImageResource(imageResource)
            imageTreino.scaleType = ImageView.ScaleType.CENTER_CROP

            cardView.setOnClickListener { onItemClick(treino) }
            buttonIniciar.setOnClickListener { onButtonClick(treino) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreinoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treino_meus_treinos, parent, false)
        return TreinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreinoViewHolder, position: Int) {
        holder.bind(treinos[position])
    }

    override fun getItemCount(): Int = treinos.size
}