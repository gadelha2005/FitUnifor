package com.example.fitunifor.administrador.fichas

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R

class FiltroMusculoDialogFragment : DialogFragment() {

    interface OnFiltroAplicadoListener {
        fun onFiltroAplicado(musculos: List<String>)
    }

    private var listener: OnFiltroAplicadoListener? = null
    private val musculosSelecionados = mutableListOf<String>()

    fun setOnFiltroAplicadoListener(listener: OnFiltroAplicadoListener) {
        this.listener = listener
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_filtro_musculo, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_musculos)
        val buttonConfirmar = view.findViewById<Button>(R.id.button_confirmar_filtro)
        val editTextBusca = view.findViewById<EditText>(R.id.edit_text_busca_musculo)



        val todosMusculos = listOf(
            Musculo("Costas", R.drawable.icon_back_body),
            Musculo("Bíceps", R.drawable.icon_warms),
            Musculo("Peito", R.drawable.icon_chest)
        )

        val adapter = MusculoAdapter(todosMusculos) { musculo, isChecked ->
            if (isChecked) {
                musculosSelecionados.add(musculo.nome)
            } else {
                musculosSelecionados.remove(musculo.nome)
            }
            buttonConfirmar.visibility =
                if (musculosSelecionados.isNotEmpty()) View.VISIBLE else View.GONE
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        buttonConfirmar.setOnClickListener {
            listener?.onFiltroAplicado(musculosSelecionados.toList())
            dismiss()
        }

        editTextBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

}