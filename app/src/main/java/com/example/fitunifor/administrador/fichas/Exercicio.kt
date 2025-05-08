package com.example.fitunifor.administrador.fichas

import android.os.Parcel
import android.os.Parcelable

data class Exercicio(
    val id: Int,
    val nome: String,
    val grupoMuscular: String,
    val imagemUrl: String? = null,
    var series: MutableList<Serie> = mutableListOf(Serie(1, 0.0, 0))
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        nome = parcel.readString() ?: "",
        grupoMuscular = parcel.readString() ?: "",
        imagemUrl = parcel.readString(),
        series = mutableListOf<Serie>().apply {
            parcel.readTypedList(this, Serie.CREATOR)
        }
    )

    constructor(nome: String, grupoMuscular: String) : this(
        id = 0,
        nome = nome,
        grupoMuscular = grupoMuscular,
        imagemUrl = null,
        series = mutableListOf(Serie(1, 0.0, 0))
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nome)
        parcel.writeString(grupoMuscular)
        parcel.writeString(imagemUrl)
        parcel.writeTypedList(series)
    }

    override fun describeContents(): Int = 0

    override fun toString(): String = "$nome ($grupoMuscular) - ${series.size} séries"

    fun adicionarSerie() {
        series.add(Serie(series.size + 1, 0.0, 0))
    }

    fun removerSerie(index: Int) {
        if (index in series.indices) {
            series.removeAt(index)
            // Reorganiza os números das séries
            series.forEachIndexed { i, serie -> serie.numero = i + 1 }
        }
    }

    companion object CREATOR : Parcelable.Creator<Exercicio> {
        override fun createFromParcel(parcel: Parcel): Exercicio = Exercicio(parcel)
        override fun newArray(size: Int): Array<Exercicio?> = arrayOfNulls(size)
    }
}