package com.example.fitunifor.administrador.fichas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class TreinoAdapterAdmin(
    private val treinos: List<Treino>,
    private val onEditarClick: (Treino) -> Unit,
    private val onRemoverClick: (Treino) -> Unit
) : RecyclerView.Adapter<TreinoAdapterAdmin.TreinoViewHolder>() {

    inner class TreinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNome: TextView = itemView.findViewById(R.id.text_nome_treino)
        val textQtdExercicios: TextView = itemView.findViewById(R.id.text_numero_exercicios_treino)
        val textDia: TextView = itemView.findViewById(R.id.text_dia_treino)
        val cardView: CardView = itemView.findViewById(R.id.card_treino)
        val btnEditar: Button = itemView.findViewById(R.id.button_editar_treino)
        val btnRemover: Button = itemView.findViewById(R.id.button_remover_treino)
        val imageTreino: ImageView = itemView.findViewById(R.id.image_treino1) // ID específico neste layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreinoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treino_admin, parent, false)
        return TreinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreinoViewHolder, position: Int) {
        val treino = treinos[position]

        with(holder) {
            textNome.text = treino.titulo
            textQtdExercicios.text = "${treino.getQuantidadeExercicios()} exercícios"
            textDia.text = treino.diaDaSemana

            // Definir a imagem com base no tipo de treino
            val imageResource = when (treino.determinarImagemTreino()) {
                "treino_peito" -> R.drawable.treino_peito
                "treino_costas" -> R.drawable.treino_costas
                "treino_perna" -> R.drawable.treino_perna
                else -> R.drawable.treino
            }
            imageTreino.setImageResource(imageResource)
            imageTreino.scaleType = ImageView.ScaleType.CENTER_CROP

            btnEditar.setOnClickListener { onEditarClick(treino) }
            btnRemover.setOnClickListener { onRemoverClick(treino) }
        }
    }

    override fun getItemCount(): Int = treinos.size
}