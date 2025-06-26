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
import com.example.fitunifor.administrador.fichas.Treino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class MeusTreinosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TreinoAdapterAluno
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meus_treinos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_treinos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializa o adapter com lista vazia
        adapter = TreinoAdapterAluno(emptyList(),
            onItemClick = { treino ->
                navegarParaTelaTreino(treino)
            },
            onButtonClick = { treino ->
                navegarParaTreinoIniciado(treino)
            }
        )

        recyclerView.adapter = adapter

        // Buscar treinos do aluno atual
        carregarTreinosDoAluno()
    }

    private fun carregarTreinosDoAluno() {
        val alunoId = auth.currentUser?.uid ?: return

        db.collection("treinos")
            .whereEqualTo("alunoId", alunoId)
            .get()
            .addOnSuccessListener { documents ->
                val treinos = documents.map { doc ->
                    doc.toObject<Treino>().copy(id = doc.id)
                }
                adapter = TreinoAdapterAluno(treinos,
                    onItemClick = { treino ->
                        navegarParaTelaTreino(treino)
                    },
                    onButtonClick = { treino ->
                        navegarParaTreinoIniciado(treino)
                    }
                )
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Tratar erro
            }
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