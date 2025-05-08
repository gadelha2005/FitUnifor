package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.administrador.fichas.Exercicio
import com.example.fitunifor.administrador.fichas.Treino

class MeusTreinosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meus_treinos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Exemplo de lista de treinos - você deve substituir por seus dados reais
        val listaTreinos = listOf(
            Treino(
                id = 1,
                alunoId = "1",
                titulo = "Treino A: Peito + Ombro + Tríceps",
                diaDaSemana = "Segunda",
                exercicios = listOf(
                    Exercicio("Supino Reto", "Peito"),
                    Exercicio("Supino Inclinado", "Peito"),
                    Exercicio("Desenvolvimento", "Ombro"),
                    Exercicio("Elevação Lateral", "Ombro"),
                    Exercicio("Tríceps Corda", "Tríceps"),
                    Exercicio("Tríceps Francês", "Tríceps")
                )
            ),
            Treino(
                id = 2,
                alunoId = "1",
                titulo = "Treino B: Costas + Bíceps",
                diaDaSemana = "Terça",
                exercicios = listOf(
                    Exercicio("Puxada Alta", "Costas"),
                    Exercicio("Remada Curvada", "Costas"),
                    Exercicio("Puxada Frente", "Costas"),
                    Exercicio("Rosca Direta", "Bíceps"),
                    Exercicio("Rosca Alternada", "Bíceps")
                )
            ),
            Treino(
                id = 3,
                alunoId = "1",
                titulo = "Treino C: Perna",
                diaDaSemana = "Quinta",
                exercicios = listOf(
                    Exercicio("Agachamento Livre", "Perna"),
                    Exercicio("Leg Press", "Perna"),
                    Exercicio("Cadeira Extensora", "Perna"),
                    Exercicio("Mesa Flexora", "Perna"),
                    Exercicio("Panturrilha Sentado", "Perna"),
                    Exercicio("Panturrilha em Pé", "Perna")
                )
            )
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_treinos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = TreinoAdapterAluno(listaTreinos,
            onItemClick = { treino ->
                navegarParaTelaTreino(treino)
            },
            onButtonClick = { treino ->
                navegarParaTreinoIniciado(treino)
            }
        )

        recyclerView.adapter = adapter
    }

    private fun navegarParaTelaTreino(treino: Treino) {
        startActivity(Intent(requireActivity(), TreinoActivity::class.java).apply {
            putExtra("TREINO", treino)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        applyTransition()
    }

    private fun navegarParaTreinoIniciado(treino: Treino) {
        startActivity(Intent(requireActivity(), TreinoIniciadoActivity::class.java).apply {
            putExtra("TREINO", treino)
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