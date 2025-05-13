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
    var corFundo: String = "#E9F7FF",
    var exercicios: List<Exercicio> = emptyList()
) : Parcelable {

    // Construtor sem argumentos necessário para o Firebase
    constructor() : this("", "", "", "", "#E9F7FF", emptyList())

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        alunoId = parcel.readString() ?: "",
        titulo = parcel.readString() ?: "",
        diaDaSemana = parcel.readString() ?: "",
        corFundo = parcel.readString() ?: "#E9F7FF",
        exercicios = parcel.createTypedArrayList(Exercicio.CREATOR) ?: emptyList()
    )

    fun getQuantidadeExercicios(): Int = exercicios.size

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(alunoId)
        parcel.writeString(titulo)
        parcel.writeString(diaDaSemana)
        parcel.writeString(corFundo)
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