package com.example.fitunifor.aluno

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ExercicioFinalizado(
    val nome: String = "",
    val concluido: Boolean = true,
    val series: List<SerieFinalizada> = emptyList()
) : Parcelable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "concluido" to concluido,
            "series" to series.map { it.toMap() }
        )
    }

    // Construtor vazio necessário para o Firebase
    constructor() : this("", true, emptyList())
}