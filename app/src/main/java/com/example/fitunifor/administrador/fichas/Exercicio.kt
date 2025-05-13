package com.example.fitunifor.administrador.fichas

import android.os.Parcel
import android.os.Parcelable

data class Exercicio(
    var id: String = "",  // Mudado para String (Firebase usa IDs como string)
    val nome: String = "",
    val grupoMuscular: String = "",
    val imagemUrl: String? = null,
    val videoUrl: String? = null,
    var series: MutableList<Serie> = mutableListOf(Serie(1, 0.0, 0))
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",  // Alterado para readString
        nome = parcel.readString() ?: "",
        grupoMuscular = parcel.readString() ?: "",
        imagemUrl = parcel.readString(),
        videoUrl = parcel.readString(),
        series = mutableListOf<Serie>().apply {
            parcel.readTypedList(this, Serie.CREATOR)
        }
    )

    constructor(nome: String, grupoMuscular: String) : this(
        id = "",  // Alterado para string vazia
        nome = nome,
        grupoMuscular = grupoMuscular,
        imagemUrl = null,
        videoUrl = null,
        series = mutableListOf(Serie(1, 0.0, 0))
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)  // Alterado para writeString
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