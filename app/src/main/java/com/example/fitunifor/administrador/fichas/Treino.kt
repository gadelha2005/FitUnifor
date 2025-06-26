package com.example.fitunifor.administrador.fichas

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class Treino(
    var id: String = "",
    var alunoId: String = "",
    var titulo: String = "",
    var diaDaSemana: String = "",
    var imagemTreino: String = "treino", // Padrão será a imagem genérica
    var exercicios: List<Exercicio> = emptyList()
) : Parcelable {

    // Construtor sem argumentos necessário para o Firebase
    constructor() : this("", "", "", "", "treino", emptyList())

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        alunoId = parcel.readString() ?: "",
        titulo = parcel.readString() ?: "",
        diaDaSemana = parcel.readString() ?: "",
        imagemTreino = parcel.readString() ?: "treino",
        exercicios = parcel.createTypedArrayList(Exercicio.CREATOR) ?: emptyList()
    )

    fun getQuantidadeExercicios(): Int = exercicios.size

    // Função para determinar a imagem com base nos exercícios
    fun determinarImagemTreino(): String {
        if (exercicios.isEmpty()) return "treino"

        val gruposMusculares = exercicios.map { it.grupoMuscular?.lowercase() ?: "" }

        return when {
            gruposMusculares.any { it.contains("peito") } -> "treino_peito"
            gruposMusculares.any { it.contains("costas") } -> "treino_costas"
            gruposMusculares.any { it.contains("perna") || it.contains("quadríceps") || it.contains("posterior") } -> "treino_perna"
            else -> "treino"
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(alunoId)
        parcel.writeString(titulo)
        parcel.writeString(diaDaSemana)
        parcel.writeString(imagemTreino)
        parcel.writeTypedList(exercicios)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Treino> {
        override fun createFromParcel(parcel: Parcel): Treino {
            return Treino(parcel)
        }

        override fun newArray(size: Int): Array<Treino?> {
            return arrayOfNulls(size)
        }
    }
}