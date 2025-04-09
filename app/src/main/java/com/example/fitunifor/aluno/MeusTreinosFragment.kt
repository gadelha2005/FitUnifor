package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.fitunifor.R

class MeusTreinosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meus_treinos, container, false)

        // Configuração dos listeners
        setupClickListeners(view)

        return view
    }

    private fun setupClickListeners(view: View) {
        // Listener para o card_treino1 - vai para Tela de Treino (detalhes)
        view.findViewById<CardView>(R.id.card_treino1).setOnClickListener {
            navegarParaTelaTreino()
        }

        // Listener para o button_iniciar_treino1 - vai para Tela de Treino Iniciado (execução)
        view.findViewById<Button>(R.id.button_iniciar_treino1).setOnClickListener {
            navegarParaTreinoIniciado()
        }
    }

    private fun navegarParaTelaTreino() {
        startActivity(Intent(requireActivity(), TreinoActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
                applyTransition()
    }

    private fun navegarParaTreinoIniciado() {
        startActivity(Intent(requireActivity(), TreinoIniciadoActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        applyTransition()
    }

    private fun applyTransition() {
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}