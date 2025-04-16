package com.example.fitunifor.administrador.aulas

interface NovaAulaListener {
    fun onAulaCriada(
        nomeAula: String,
        nomeProfessor: String,
        diaSemana: String,
        horario: String,
        capacidade: Int
    )
}
