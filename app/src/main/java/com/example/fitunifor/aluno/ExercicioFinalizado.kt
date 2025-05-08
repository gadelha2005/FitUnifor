package com.example.fitunifor.aluno

import android.os.Parcel
import android.os.Parcelable

data class ExercicioFinalizado(
    val nome: String,
    val concluido: Boolean,
    val series: List<SerieFinalizada>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.createTypedArrayList(SerieFinalizada.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeByte(if (concluido) 1 else 0)
        parcel.writeTypedList(series)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ExercicioFinalizado> {
        override fun createFromParcel(parcel: Parcel): ExercicioFinalizado {
            return ExercicioFinalizado(parcel)
        }

        override fun newArray(size: Int): Array<ExercicioFinalizado?> {
            return arrayOfNulls(size)
        }
    }
}
