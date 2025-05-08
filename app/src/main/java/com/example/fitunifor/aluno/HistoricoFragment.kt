package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.model.TreinoFinalizado

class HistoricoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_historico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dados de exemplo completos com exercícios e séries
        val listaTreinosFinalizados = listOf(
            criarTreinoPeitoOmbroTriceps(),
            criarTreinoCostasBiceps()
            // Adicione mais treinos conforme necessário
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_historico)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = TreinoHistoricoAdapter(listaTreinosFinalizados) { treino ->
            navegarParaTreinoFinalizado(treino)
        }

        recyclerView.adapter = adapter
    }

    private fun criarTreinoPeitoOmbroTriceps(): TreinoFinalizado {
        return TreinoFinalizado(
            id = 1,
            titulo = "Treino A: Peito + Ombro + Tríceps",
            data = "24/02/2024",
            exerciciosCompletos = 3,
            totalExercicios = 3,
            exercicios = listOf(
                ExercicioFinalizado(
                    nome = "Supino Inclinado com Halteres",
                    concluido = true,
                    series = listOf(
                        SerieFinalizada(1, 8, 30.0),
                        SerieFinalizada(2, 8, 30.0),
                        SerieFinalizada(3, 6, 30.0)
                    )
                ),
                ExercicioFinalizado(
                    nome = "Elevação Lateral",
                    concluido = true,
                    series = listOf(
                        SerieFinalizada(1, 8, 10.0),
                        SerieFinalizada(2, 8, 10.0),
                        SerieFinalizada(3, 8, 10.0)
                    )
                ),
                ExercicioFinalizado(
                    nome = "Tríceps Corda",
                    concluido = true,
                    series = listOf(
                        SerieFinalizada(1, 8, 25.0),
                        SerieFinalizada(2, 8, 25.0),
                        SerieFinalizada(3, 8, 25.0)
                    )
                )
            )
        )
    }

    private fun criarTreinoCostasBiceps(): TreinoFinalizado {
        return TreinoFinalizado(
            id = 2,
            titulo = "Treino B: Costas + Bíceps",
            data = "26/02/2024",
            exerciciosCompletos = 3,
            totalExercicios = 5,
            exercicios = listOf(
                ExercicioFinalizado(
                    nome = "Barra Fixa",
                    concluido = true,
                    series = listOf(
                        SerieFinalizada(1, 8, 0.0), // Peso corporal
                        SerieFinalizada(2, 6, 0.0),
                        SerieFinalizada(3, 5, 0.0)
                    )
                ),
                ExercicioFinalizado(
                    nome = "Remada Curvada",
                    concluido = true,
                    series = listOf(
                        SerieFinalizada(1, 8, 40.0),
                        SerieFinalizada(2, 8, 40.0),
                        SerieFinalizada(3, 6, 40.0)
                    )
                ),
                ExercicioFinalizado(
                    nome = "Rosca Direta",
                    concluido = false,
                    series = listOf(
                        SerieFinalizada(1, 8, 12.0),
                        SerieFinalizada(2, 8, 12.0),
                        SerieFinalizada(3, 0, 0.0) // Não realizado
                    )
                )
            )
        )
    }

    private fun navegarParaTreinoFinalizado(treino: TreinoFinalizado) {
        val intent = Intent(requireContext(), TreinoFinalizadoActivity::class.java).apply {
            putExtra("TREINO_FINALIZADO", treino)
        }
        startActivity(intent)
    }
}