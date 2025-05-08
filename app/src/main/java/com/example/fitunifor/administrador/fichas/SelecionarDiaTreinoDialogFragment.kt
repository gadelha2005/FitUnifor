package com.example.fitunifor.administrador.fichas

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.fitunifor.R

class SelecionarDiaTreinoDialogFragment : DialogFragment() {
    interface OnDiaSelecionadoListener {
        fun onDiaSelecionado(dia: String)
    }

    private var listener: OnDiaSelecionadoListener? = null

    fun setOnDiaSelecionadoListener(listener: OnDiaSelecionadoListener) {
        this.listener = listener
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_selecionar_dia_treino, container, false)

        val checkBoxes = listOf(
            view.findViewById<CheckBox>(R.id.check_box_segunda),
            view.findViewById<CheckBox>(R.id.check_box_terca),
            view.findViewById<CheckBox>(R.id.check_box_quarta),
            view.findViewById<CheckBox>(R.id.check_box_quinta),
            view.findViewById<CheckBox>(R.id.check_box_sexta),
            view.findViewById<CheckBox>(R.id.check_box_sabado)
        )

        val botaoConfirmar = view.findViewById<Button>(R.id.botao_confirmar)
        botaoConfirmar.visibility = View.GONE

        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkBoxes.forEach { other ->
                        if (other != checkBox) other.isChecked = false
                    }
                    botaoConfirmar.visibility = View.VISIBLE
                } else if (checkBoxes.none { it.isChecked }) {
                    botaoConfirmar.visibility = View.GONE
                }
            }
        }

        botaoConfirmar.setOnClickListener {
            val diaSelecionado = when {
                checkBoxes[0].isChecked -> "Segunda"
                checkBoxes[1].isChecked -> "Terça"
                checkBoxes[2].isChecked -> "Quarta"
                checkBoxes[3].isChecked -> "Quinta"
                checkBoxes[4].isChecked -> "Sexta"
                checkBoxes[5].isChecked -> "Sábado"
                else -> ""
            }
            listener?.onDiaSelecionado(diaSelecionado)
            dismiss()
        }

        view.findViewById<ImageView>(R.id.icon_close_selecionar_dia).setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false) // Impede fechar ao clicar fora
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false) // Impede fechar com botão voltar
    }
}