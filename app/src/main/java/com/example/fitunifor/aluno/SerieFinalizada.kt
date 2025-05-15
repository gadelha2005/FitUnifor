package com.example.fitunifor.aluno

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SerieFinalizada(
    val numero: Int = 0,
    val repeticoes: Int = 0,
    val carga: Double = 0.0
) : Parcelable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "numero" to numero,
            "repeticoes" to repeticoes,
            "carga" to carga
        )
    }

    // Construtor vazio necessário para o Firebase
    constructor() : this(0, 0, 0.0)
}