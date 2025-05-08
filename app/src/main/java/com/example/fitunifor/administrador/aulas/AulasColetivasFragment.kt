package com.example.fitunifor.administrador.aulas

import Aula
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitunifor.R
import com.example.fitunifor.databinding.FragmentAulasColetivasBinding
import com.google.firebase.firestore.FirebaseFirestore

class AulasColetivasFragment : Fragment(R.layout.fragment_aulas_coletivas) {
    private var _binding: FragmentAulasColetivasBinding? = null
    private val binding get() = _binding!!
    private lateinit var aulaAdapter: AulaAdapterAdmin
    private val db = FirebaseFirestore.getInstance()
    private val todasAulas = mutableListOf<Aula>()
    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAulasColetivasBinding.bind(view)

        setupRecyclerView()
        setupListeners()
        carregarAulasDoFirestore()
    }

    private fun setupRecyclerView() {
        aulaAdapter = AulaAdapterAdmin(
            mutableListOf(),
            onEditarClick = { aula -> editarAula(aula) },
            onRemoverClick = { aulaId -> removerAulaDoFirestore(aulaId) }
        )

        binding.recyclerAulas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = aulaAdapter
            setHasFixedSize(true)
        }
    }

    private fun carregarAulasDoFirestore() {
        db.collection("aulas")
            .get()
            .addOnSuccessListener { querySnapshot ->
                todasAulas.clear()
                val aulas = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Aula::class.java)?.copy(id = doc.id)
                }
                todasAulas.addAll(aulas)
                aulaAdapter.atualizarLista(aulas)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao carregar aulas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filtrarAulas(texto: String) {
        if (texto.isEmpty()) {
            aulaAdapter.atualizarLista(todasAulas)
            return
        }

        val aulasFiltradas = todasAulas.filter { aula ->
            aula.nome.contains(texto, true) ||
                    aula.professor.contains(texto, true) ||
                    aula.diaSemana.contains(texto, true) ||
                    aula.horario.contains(texto, true)
        }

        aulaAdapter.atualizarLista(aulasFiltradas)
    }

    private fun removerAulaDoFirestore(aulaId: String) {
        db.collection("aulas").document(aulaId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Aula removida com sucesso!", Toast.LENGTH_SHORT).show()
                carregarAulasDoFirestore()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao remover: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupListeners() {
        binding.buttonAdicionarAula.setOnClickListener {
            showNovaAulaDialog()
        }

        binding.editTextBuscarAula.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    filtrarAulas(s?.toString() ?: "")
                }
                handler.postDelayed(searchRunnable!!, 300)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showNovaAulaDialog() {
        NovaAulaDialogFragment.newInstance().apply {
            setListener(object : NovaAulaDialogFragment.AulaDialogListener {
                override fun onAulaSalva(aula: Aula) {
                    carregarAulasDoFirestore()
                }
                override fun onAulaAtualizada(aula: Aula) {
                    carregarAulasDoFirestore()
                }
            })
        }.show(childFragmentManager, "NovaAulaDialog")
    }

    private fun editarAula(aula: Aula) {
        NovaAulaDialogFragment.newInstance(aula).apply {
            setListener(object : NovaAulaDialogFragment.AulaDialogListener {
                override fun onAulaSalva(aula: Aula) {
                    carregarAulasDoFirestore()
                }
                override fun onAulaAtualizada(aula: Aula) {
                    carregarAulasDoFirestore()
                }
            })
        }.show(childFragmentManager, "EditarAulaDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }
}
