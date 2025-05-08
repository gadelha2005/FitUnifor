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
        try {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_exercicio_novo_treino_aluno, parent, false)
            return ExercicioNoTreinoViewHolder(view)
        } catch (e: Exception) {
            Log.e("Adapter", "Erro ao criar ViewHolder", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: ExercicioNoTreinoViewHolder, position: Int) {
        try {
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
        } catch (e: Exception) {
            Log.e("Adapter", "Erro no onBindViewHolder", e)
        }
    }

    private fun configurarSeries(container: LinearLayout, exercicio: Exercicio) {
        container.removeAllViews()

        exercicio.series.forEachIndexed { index, serie ->
            try {
                val serieView = LayoutInflater.from(container.context)
                    .inflate(R.layout.item_serie, container, false)

                val textSerieNumero = serieView.findViewById<TextView>(R.id.textSerieNumero)
                val editPeso = serieView.findViewById<EditText>(R.id.text_edit_peso)
                val editReps = serieView.findViewById<EditText>(R.id.text_edit_reps)
                val iconApagarSerie = serieView.findViewById<ImageView>(R.id.icon_apagar_serie)

                textSerieNumero.text = "${index + 1}ª série"

                if (serie.peso != 0.0) {
                    editPeso.setText(serie.peso.toString())
                } else {
                    editPeso.setText("")
                }

                if (serie.repeticoes != 0) {
                    editReps.setText(serie.repeticoes.toString())
                } else {
                    editReps.setText("")
                }

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

                // 👉 Mostrar botão apagar só se não for a primeira série
                if (index == 0) {
                    iconApagarSerie.visibility = View.GONE
                } else {
                    iconApagarSerie.visibility = View.VISIBLE
                    iconApagarSerie.setOnClickListener {
                        exercicio.series.removeAt(index)
                        notifyDataSetChanged() // Redesenha tudo, simples e funcional
                    }
                }

                container.addView(serieView)
            } catch (e: Exception) {
                Log.e("Adapter", "Erro ao configurar série", e)
            }
        }
    }


    override fun getItemCount(): Int = exercicios.size
}