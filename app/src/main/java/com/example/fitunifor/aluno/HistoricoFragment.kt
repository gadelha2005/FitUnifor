package com.example.fitunifor.aluno


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.example.fitunifor.model.TreinoFinalizado
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class HistoricoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: TreinoHistoricoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_historico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HistoricoFragment", "Fragment criado")

        recyclerView = view.findViewById(R.id.recycler_historico)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializa o adapter vazio
        adapter = TreinoHistoricoAdapter(emptyList()) { treino ->
            navegarParaTreinoFinalizado(treino)
        }
        recyclerView.adapter = adapter

        carregarTreinosDoFirebase()
    }

    private fun carregarTreinosDoFirebase() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("HistoricoFragment", "Carregando treinos para o usuário: $userId")

        db.collection("treinosFinalizados")
            .whereEqualTo("userId", userId)
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("HistoricoFragment", "Nenhum treino encontrado")
                    Toast.makeText(requireContext(), "Nenhum treino no histórico", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val treinos = documents.map { doc ->
                    doc.toObject<TreinoFinalizado>().copy(id = doc.id)
                }

                Log.d("HistoricoFragment", "${treinos.size} treinos carregados")
                adapter.updateList(treinos) // Método renomeado para updateList
            }
            .addOnFailureListener { exception ->
                Log.e("HistoricoFragment", "Erro ao carregar treinos", exception)
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar histórico: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun navegarParaTreinoFinalizado(treino: TreinoFinalizado) {
        val intent = TreinoFinalizadoActivity.newIntent(requireContext(), treino)
        startActivity(intent)
    }
}