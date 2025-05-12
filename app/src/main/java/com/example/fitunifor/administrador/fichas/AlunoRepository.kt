package com.example.fitunifor.administrador.fichas

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class AlunoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val alunosCollection = db.collection("usuarios") // Assumindo que sua coleção se chama "usuarios"

    suspend fun getAlunos(): List<Aluno> {
        return try {
            val querySnapshot = alunosCollection
                .whereEqualTo("tipo", "aluno") // Filtra apenas usuários com tipo "aluno"
                .get()
                .await()

            querySnapshot.toAlunoList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun QuerySnapshot.toAlunoList(): List<Aluno> {
        return this.documents.map { document ->
            Aluno(
                id = document.id,
                nome = document.getString("nome") ?: "",
                email = document.getString("email") ?: "",
                tipo = document.getString("tipo") ?: ""
            )
        }
    }
}