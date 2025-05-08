package com.example.fitunifor.administrador.fichas

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class ExercicioAdapter(
    private var exercicios: List<Exercicio>,
    private val onExercicioSelecionado: (Exercicio, Boolean) -> Unit
) : RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder>() {

    private val selecionados = mutableMapOf<Int, Boolean>()
    private var listaCompleta = exercicios

    inner class ExercicioViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_adicionar_exercicio, parent, false)
    ) {
        val imageView: ImageView = itemView.findViewById(R.id.image_exercicio)
        val textNome: TextView = itemView.findViewById(R.id.text_nome_exercicio)
        val textMusculo: TextView = itemView.findViewById(R.id.text_musculo_exercicio)
        val checkBox: CheckBox = itemView.findViewById(R.id.check_box_selecionar_exercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        return ExercicioViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = exercicios[position]

        with(holder) {
            textNome.text = exercicio.nome
            textMusculo.text = exercicio.grupoMuscular

            imageView.setImageResource(
                when (exercicio.grupoMuscular) {
                    "Peito" -> R.drawable.icon_chest
                    "Pernas" -> R.drawable.icon_legs
                    "Costas" -> R.drawable.icon_back_body
                    "Bíceps", "Tríceps" -> R.drawable.icon_warms
                    else -> R.drawable.icon_body
                }
            )

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = selecionados[exercicio.id] == true

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                selecionados[exercicio.id] = isChecked
                onExercicioSelecionado(exercicio, isChecked)
            }
        }
    }

    override fun getItemCount(): Int = exercicios.size

    fun atualizarLista(novaLista: List<Exercicio>) {
        exercicios = novaLista
        notifyDataSetChanged()
    }

    fun filter(text: String) {
        val filteredList = if (text.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nome.contains(text, ignoreCase = true) ||
                        it.grupoMuscular.contains(text, ignoreCase = true)
            }
        }
        exercicios = filteredList
        notifyDataSetChanged()
    }

    fun getSelecionados(): List<Exercicio> {
        return exercicios.filter { selecionados[it.id] == true }
    }

    fun setFiltroMusculos(musculos: List<String>) {
        exercicios = if (musculos.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter { exercicio ->
                musculos.any { grupo ->
                    exercicio.grupoMuscular.contains(grupo, ignoreCase = true)
                }
            }
        }
        notifyDataSetChanged()
    }
}