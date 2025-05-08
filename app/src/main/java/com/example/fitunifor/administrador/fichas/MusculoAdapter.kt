package com.example.fitunifor.administrador.fichas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class MusculoAdapter(
    private var musculos: List<Musculo>,
    private val onMusculoSelecionado: (Musculo, Boolean) -> Unit
) : RecyclerView.Adapter<MusculoAdapter.MusculoViewHolder>() {

    private var listaCompleta = musculos

    inner class MusculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_musculo)
        val textView: TextView = itemView.findViewById(R.id.text_nome_musculo)
        val checkBox: CheckBox = itemView.findViewById(R.id.check_box_musculo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusculoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_musculos, parent, false)
        return MusculoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusculoViewHolder, position: Int) {
        val musculo = musculos[position]

        holder.textView.text = musculo.nome
        holder.imageView.setImageResource(musculo.icone)
        holder.checkBox.isChecked = musculo.selecionado

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            musculo.selecionado = isChecked
            onMusculoSelecionado(musculo, isChecked)
        }
    }

    override fun getItemCount(): Int = musculos.size

    fun filter(text: String) {
        musculos = if (text.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nome.contains(text, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}