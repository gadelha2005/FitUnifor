package com.example.fitunifor.administrador.aulas

import Aula
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class AulaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val aulasCollection = db.collection("aulas")

    fun getAulas(onSuccess: (List<Aula>) -> Unit, onFailure: (Exception) -> Unit) {
        aulasCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val aulas = querySnapshot.documents.map { doc ->
                    doc.toObject<Aula>()?.copy(id = doc.id) ?: throw IllegalStateException("Aula inválida")
                }
                onSuccess(aulas)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addAula(aula: Aula, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        aulasCollection.document(aula.id).set(aula)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateAula(aula: Aula, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        aulasCollection.document(aula.id).set(aula)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteAula(aulaId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        aulasCollection.document(aulaId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}