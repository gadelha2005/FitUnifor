package com.example.fitunifor.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.example.fitunifor.R
import com.example.fitunifor.aluno.ExercicioFinalizado
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class TreinoFinalizado(
    val id: String = "",
    val userId: String = "",
    val titulo: String = "",
    val data: String = "",
    val exerciciosCompletos: Int = 0,
    val totalExercicios: Int = 0,
    val exercicios: List<ExercicioFinalizado> = emptyList()
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "titulo" to titulo,
            "data" to data,
            "exerciciosCompletos" to exerciciosCompletos,
            "totalExercicios" to totalExercicios,
            "exercicios" to exercicios.map { it.toMap() }
        )
    }

    fun determinarImagemTreino(): Int {
        return when {
            titulo.contains("Peito", ignoreCase = true) -> R.drawable.treino_peito
            titulo.contains("Costas", ignoreCase = true) -> R.drawable.treino_costas
            titulo.contains("Perna", ignoreCase = true) -> R.drawable.treino_perna
            else -> R.drawable.treino
        }
    }

    // Construtor vazio necessário para o Firebase
    constructor() : this("", "", "", "", 0, 0, emptyList())
}