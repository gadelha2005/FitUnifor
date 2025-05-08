package com.example.fitunifor.administrador.fichas

import android.os.Parcel
import android.os.Parcelable

data class Treino(
    val id: Int,
    val alunoId: String,
    val titulo: String,
    val diaDaSemana: String,
    val corFundo: String = "#E9F7FF",
    val exercicios: List<Exercicio>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        alunoId = parcel.readString() ?: "",
        titulo = parcel.readString() ?: "",
        diaDaSemana = parcel.readString() ?: "",
        corFundo = parcel.readString() ?: "#E9F7FF",
        exercicios = parcel.createTypedArrayList(Exercicio) ?: emptyList()
    )

    fun getQuantidadeExercicios(): Int = exercicios.size

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
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