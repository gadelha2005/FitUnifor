package com.example.fitunifor.aluno

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmacaoTreinoDialogFragment : DialogFragment() {
    interface ConfirmacaoTreinoListener {
        fun onContinuarTreino()
        fun onCancelarTreino()
    }

    private var listener: ConfirmacaoTreinoListener? = null

    fun setListener(listener: ConfirmacaoTreinoListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Treino em andamento")
            .setMessage("Você tem um treino em andamento. Deseja continuar ou cancelar?")
            .setPositiveButton("Continuar") { _, _ ->
                listener?.onContinuarTreino()
                dismiss()
            }
            .setNegativeButton("Cancelar Treino") { _, _ ->
                listener?.onCancelarTreino()
                dismiss()
            }
            .create()
    }
}