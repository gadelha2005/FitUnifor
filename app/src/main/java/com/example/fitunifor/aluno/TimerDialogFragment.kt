package com.example.fitunifor.aluno

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.fitunifor.R

class TimerDialogFragment(
    private val initialTimeInSeconds: Int,
    private val onTimeSelected: (Int) -> Unit
) : DialogFragment() {

    protected var currentTime = initialTimeInSeconds

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_timer)

        val timerText = dialog.findViewById<TextView>(R.id.text_timer_value1)
        val btnMinus = dialog.findViewById<Button>(R.id.btn_minus1)
        val btnPlus = dialog.findViewById<Button>(R.id.btn_plus1)
        val btnSet = dialog.findViewById<Button>(R.id.btn_set_timer1)
        val icon_close = dialog.findViewById<ImageView>(R.id.icon_close)

        fun updateText() {
            val minutes = currentTime / 60
            val seconds = currentTime % 60
            timerText.text = String.format("%02d:%02d", minutes, seconds)
        }

        updateText()

        btnMinus.setOnClickListener {
            if (currentTime > 15) { // Agora o mínimo é 15 segundos
                currentTime -= 15
                updateText()
            }
        }

        btnPlus.setOnClickListener {
            if (currentTime < 300) {
                currentTime += 15
                updateText()
            }
        }


        btnSet.setOnClickListener {
            onTimeSelected(currentTime)
            dismiss()
        }

        icon_close.setOnClickListener{

        }

        icon_close.setOnClickListener{
            dismiss()
        }


        return dialog
    }


}

