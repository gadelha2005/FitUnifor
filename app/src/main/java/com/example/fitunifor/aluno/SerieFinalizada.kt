package com.example.fitunifor.aluno

import android.os.Parcel
import android.os.Parcelable

data class SerieFinalizada(
    val numero: Int,
    val repeticoes: Int,
    val peso: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(numero)
        parcel.writeInt(repeticoes)
        parcel.writeDouble(peso)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SerieFinalizada> {
        override fun createFromParcel(parcel: Parcel): SerieFinalizada {
            return SerieFinalizada(parcel)
        }

        override fun newArray(size: Int): Array<SerieFinalizada?> {
            return arrayOfNulls(size)
        }
    }
}