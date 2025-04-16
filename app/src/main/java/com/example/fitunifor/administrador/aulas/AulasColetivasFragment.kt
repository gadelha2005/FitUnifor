package com.example.fitunifor.administrador.aulas

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.fitunifor.R

class AulasColetivasFragment : Fragment(R.layout.fragment_aulas_coletivas) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val botaoAdicionarAula = view.findViewById<Button>(R.id.button_adicionar_aula)
        botaoAdicionarAula.setOnClickListener {
            val dialog = NovaAulaDialogFragment()
            dialog.show(childFragmentManager, "NovaAulaDialog")
        }

        if (savedInstanceState == null) {
            val fragmentAulaAdmin = AulaAdminFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.container_aula_admin, fragmentAulaAdmin)
                .commit()
        }
    }
}