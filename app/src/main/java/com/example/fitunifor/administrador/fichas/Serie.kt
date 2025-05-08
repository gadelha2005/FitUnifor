package com.example.fitunifor.administrador.fichas

import android.os.Parcel
import android.os.Parcelable

data class Serie(
    var numero: Int,
    var peso: Double,
    var repeticoes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(numero)
        parcel.writeDouble(peso)
        parcel.writeInt(repeticoes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Serie> {
        override fun createFromParcel(parcel: Parcel): Serie = Serie(parcel)
        override fun newArray(size: Int): Array<Serie?> = arrayOfNulls(size)
    }
}