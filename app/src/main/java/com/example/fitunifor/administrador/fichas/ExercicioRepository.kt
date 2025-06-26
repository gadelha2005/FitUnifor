package com.example.fitunifor.administrador.fichas

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExercicioRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val exerciciosCollection = db.collection("exercicios")

    suspend fun getExercicios(): List<Exercicio> {
        return try {
            val querySnapshot = exerciciosCollection.get().await()
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(Exercicio::class.java)?.apply {
                    id = document.id
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}