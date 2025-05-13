package com.example.fitunifor.administrador.fichas

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class Serie(
    var numero: Int = 0,
    var peso: Double = 0.0,
    var repeticoes: Int = 0
) : Parcelable {
    // Construtor sem argumentos necessário para Firebase
    constructor() : this(0, 0.0, 0)

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