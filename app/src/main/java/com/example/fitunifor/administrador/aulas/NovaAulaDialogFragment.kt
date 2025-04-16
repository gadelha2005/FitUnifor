package com.example.fitunifor.administrador.aulas

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.fitunifor.R
import java.util.*

class NovaAulaDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_nova_aula, container, false)

        val buttonSalvarAula = view.findViewById<Button>(R.id.button_salvar_aula)
        val iconClose = view.findViewById<ImageView>(R.id.icon_close)
        val spinnerDiaSemana = view.findViewById<Spinner>(R.id.spinner_dia_semana)
        val editTextHorario = view.findViewById<EditText>(R.id.edit_text_horario)

        // Spinner: Dias da semana
        val diasSemana = listOf("Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado", "Domingo")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, diasSemana)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDiaSemana.adapter = adapter

        // EditText: Seletor de horário
        editTextHorario.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                val horarioFormatado = String.format("%02d:%02d", selectedHour, selectedMinute)
                editTextHorario.setText(horarioFormatado)
            }, hour, minute, true)

            timePicker.show()
        }

        buttonSalvarAula.setOnClickListener {
            val diaSelecionado = spinnerDiaSemana.selectedItem.toString()
            val horarioSelecionado = editTextHorario.text.toString()
            Toast.makeText(requireContext(), "Aula salva para $diaSelecionado às $horarioSelecionado!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        iconClose.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
