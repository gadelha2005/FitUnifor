package com.example.fitunifor.administrador.aulas

import Aula
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitunifor.databinding.FragmentAulaAdminBinding

class AulaAdminFragment : Fragment() {
    private lateinit var binding: FragmentAulaAdminBinding
    private lateinit var aulaAdapter: AulaAdapterAdmin
    private val listaAulasCompleta = mutableListOf<Aula>()
    private val listaAulasFiltradas = mutableListOf<Aula>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAulaAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        aulaAdapter = AulaAdapterAdmin(
            listaAulasFiltradas,
            onEditarClick = { aula -> editarAula(aula) },
            onRemoverClick = { posicao -> removerAula(posicao.toInt()) }
        )

        binding.recyclerAulas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = aulaAdapter
        }
    }

    fun adicionarAula(aula: Aula) {
        listaAulasCompleta.add(aula)
        listaAulasFiltradas.add(aula)
        aulaAdapter.notifyItemInserted(listaAulasFiltradas.size - 1)
    }

    fun filtrarAulas(texto: String) {
        listaAulasFiltradas.clear()
        if (texto.isEmpty()) {
            listaAulasFiltradas.addAll(listaAulasCompleta)
        } else {
            listaAulasFiltradas.addAll(
                listaAulasCompleta.filter { aula ->
                    aula.nome.contains(texto, true) ||
                            aula.professor.contains(texto, true) ||
                            aula.diaSemana.contains(texto, true) ||
                            aula.horario.contains(texto, true) ||
                            aula.maxAlunos.toString().contains(texto)
                }
            )
        }
        aulaAdapter.notifyDataSetChanged()
    }

    private fun editarAula(aula: Aula) {
        // Implemente a edição aqui
    }

    private fun removerAula(posicao: Int) {
        val aulaRemovida = listaAulasFiltradas[posicao]
        listaAulasCompleta.remove(aulaRemovida)
        listaAulasFiltradas.removeAt(posicao)
        aulaAdapter.notifyItemRemoved(posicao)
    }
}