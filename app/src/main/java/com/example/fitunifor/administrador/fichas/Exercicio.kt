package com.example.fitunifor.administrador.fichas

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
data class Exercicio(
    var id: String = "",
    var nome: String = "",
    var grupoMuscular: String = "",
    var imagemUrl: String? = null,
    var videoUrl: String? = null,
    var series: MutableList<Serie> = mutableListOf()
) : Parcelable {

    // Construtor sem argumentos necessário para Firebase
    constructor() : this("", "", "", null, null, mutableListOf())

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        nome = parcel.readString() ?: "",
        grupoMuscular = parcel.readString() ?: "",
        imagemUrl = parcel.readString(),
        videoUrl = parcel.readString(),
        series = mutableListOf<Serie>().apply {
            parcel.readTypedList(this, Serie.CREATOR)
        }
    )

    constructor(nome: String, grupoMuscular: String) : this(
        id = "",
        nome = nome,
        grupoMuscular = grupoMuscular,
        imagemUrl = null,
        videoUrl = null,
        series = mutableListOf(Serie(1, 0.0, 0))
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nome)
        parcel.writeString(grupoMuscular)
        parcel.writeString(imagemUrl)
        parcel.writeString(videoUrl)
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