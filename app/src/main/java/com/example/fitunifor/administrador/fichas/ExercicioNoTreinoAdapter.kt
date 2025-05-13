package com.example.fitunifor.administrador.fichas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class ExercicioNoTreinoAdapter(
    private val exercicios: List<Exercicio>,
    private val onRemoverExercicio: (Exercicio) -> Unit,
    private val onAdicionarSerie: (Exercicio) -> Unit
) : RecyclerView.Adapter<ExercicioNoTreinoAdapter.ExercicioNoTreinoViewHolder>() {

    inner class ExercicioNoTreinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNome: TextView = itemView.findViewById(R.id.text_exercicio_nome)
        val textMusculo: TextView = itemView.findViewById(R.id.text_musculo_exercicio)
        val imageMusculo: ImageView = itemView.findViewById(R.id.image_musculo)
        val btnRemover: ImageButton = itemView.findViewById(R.id.btnRemoverExercicio)
        val btnAdicionarSerie: Button = itemView.findViewById(R.id.button_adicionar_serie)
        val seriesContainer: LinearLayout = itemView.findViewById(R.id.series_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioNoTreinoViewHolder {
        return ExercicioNoTreinoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_exercicio_novo_treino_aluno, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExercicioNoTreinoViewHolder, position: Int) {
        val exercicio = exercicios[position]
        with(holder) {
            textNome.text = exercicio.nome
            textMusculo.text = exercicio.grupoMuscular

            imageMusculo.setImageResource(
                when (exercicio.grupoMuscular) {
                    "Peito" -> R.drawable.icon_chest
                    "Pernas" -> R.drawable.icon_legs
                    "Costas" -> R.drawable.icon_back_body
                    "Bíceps", "Tríceps" -> R.drawable.icon_warms
                    else -> R.drawable.icon_body
                }
            )

            btnRemover.setOnClickListener { onRemoverExercicio(exercicio) }
            btnAdicionarSerie.setOnClickListener { onAdicionarSerie(exercicio) }
            configurarSeries(seriesContainer, exercicio)
        }
    }

    private fun configurarSeries(container: LinearLayout, exercicio: Exercicio) {
        container.removeAllViews()
        exercicio.series.forEachIndexed { index, serie ->
            val serieView = LayoutInflater.from(container.context)
                .inflate(R.layout.item_serie, container, false)

            val textSerieNumero = serieView.findViewById<TextView>(R.id.textSerieNumero)
            val editPeso = serieView.findViewById<EditText>(R.id.text_edit_peso)
            val editReps = serieView.findViewById<EditText>(R.id.text_edit_reps)
            val iconApagarSerie = serieView.findViewById<ImageView>(R.id.icon_apagar_serie)

            textSerieNumero.text = "${index + 1}ª série"
            editPeso.setText(if (serie.peso != 0.0) serie.peso.toString() else "")
            editReps.setText(if (serie.repeticoes != 0) serie.repeticoes.toString() else "")

            editPeso.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    serie.peso = editPeso.text.toString().toDoubleOrNull() ?: 0.0
                }
            }

            editReps.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    serie.repeticoes = editReps.text.toString().toIntOrNull() ?: 0
                }
            }

            iconApagarSerie.visibility = if (index == 0) View.GONE else View.VISIBLE
            iconApagarSerie.setOnClickListener {
                exercicio.series.removeAt(index)
                notifyDataSetChanged()
            }

            container.addView(serieView)
        }
    }

    override fun getItemCount(): Int = exercicios.size
}