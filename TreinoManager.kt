package com.example.fitunifor.aluno

import com.example.fitunifor.administrador.fichas.Treino

object TreinoManager {
    private var treinoEmAndamento: Treino? = null
    private var timeLeftInMillis: Long = 0
    private var exerciciosConcluidos: Int = 0

    fun iniciarTreino(treino: Treino) {
        this.treinoEmAndamento = treino
        this.timeLeftInMillis = 60000 // 1 minuto padrão
        this.exerciciosConcluidos = 0
    }

    fun finalizarTreino() {
        this.treinoEmAndamento = null
        this.timeLeftInMillis = 0
        this.exerciciosConcluidos = 0
    }

    fun getTreinoEmAndamento(): Treino? = treinoEmAndamento
    fun getTimeLeft(): Long = timeLeftInMillis
    fun getExerciciosConcluidos(): Int = exerciciosConcluidos

    fun setTimeLeft(time: Long) {
        this.timeLeftInMillis = time
    }

    fun setExerciciosConcluidos(concluidos: Int) {
        this.exerciciosConcluidos = concluidos
    }
}