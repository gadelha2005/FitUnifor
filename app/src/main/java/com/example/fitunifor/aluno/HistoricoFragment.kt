package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.fitunifor.R

class HistoricoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historico, container, false)

        val cardTreino1 = view.findViewById<CardView>(R.id.card_treino_finalizado1)
        cardTreino1.setOnClickListener {
            val intent = Intent(requireContext(),TreinoFinalizadoActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
