package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Configuração do clique no card_treino1
        view.findViewById<CardView>(R.id.card_treino1).setOnClickListener {
            navegarParaTreinoIniciado()
        }

        return view
    }

    private fun navegarParaTreinoIniciado() {
        startActivity(Intent(requireActivity(), TreinoActivity::class.java))

        // Animação opcional
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}