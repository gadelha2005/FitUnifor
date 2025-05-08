package com.example.fitunifor.model

import android.os.Parcel
import android.os.Parcelable
import com.example.fitunifor.aluno.ExercicioFinalizado

data class TreinoFinalizado(
    val id: Int,
    val titulo: String,
    val data: String,
    val exerciciosCompletos: Int,
    val totalExercicios: Int,
    val exercicios: List<ExercicioFinalizado>,
    val imagemUrl: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(ExercicioFinalizado.CREATOR) ?: emptyList(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(titulo)
        parcel.writeString(data)
        parcel.writeInt(exerciciosCompletos)
        parcel.writeInt(totalExercicios)
        parcel.writeTypedList(exercicios)
        parcel.writeString(imagemUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TreinoFinalizado> {
        override fun createFromParcel(parcel: Parcel): TreinoFinalizado {
            return TreinoFinalizado(parcel)
        }

        override fun newArray(size: Int): Array<TreinoFinalizado?> {
            return arrayOfNulls(size)
        }
    }
}