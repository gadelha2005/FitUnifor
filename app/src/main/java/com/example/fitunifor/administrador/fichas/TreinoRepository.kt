package com.example.fitunifor.administrador.fichas

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TreinoRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val treinosCollection = db.collection("treinos")

    suspend fun salvarTreino(treino: Treino): Result<Unit> {
        return try {
            // Converter o treino para um mapa
            val treinoMap = hashMapOf(
                "id" to treino.id.toString(),
                "alunoId" to treino.alunoId,
                "titulo" to treino.titulo,
                "diaDaSemana" to treino.diaDaSemana,
                "imagemTreino" to treino.imagemTreino,
                "exercicios" to treino.exercicios.map { exercicio ->
                    hashMapOf(
                        "id" to exercicio.id,
                        "nome" to exercicio.nome,
                        "grupoMuscular" to exercicio.grupoMuscular,
                        "imagemUrl" to exercicio.imagemUrl,
                        "videoUrl" to exercicio.videoUrl,
                        "series" to exercicio.series.map { serie ->
                            hashMapOf(
                                "numero" to serie.numero,
                                "peso" to serie.peso,
                                "repeticoes" to serie.repeticoes
                            )
                        }
                    )
                }
            )

            // Salvar no Firestore (usando o ID do treino como ID do documento)
            treinosCollection.document(treino.id.toString())
                .set(treinoMap, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTreinosPorAluno(alunoId: String): List<Treino> {
        return try {
            val querySnapshot = treinosCollection
                .whereEqualTo("alunoId", alunoId)
                .get()
                .await()

            querySnapshot.documents.map { document ->
                val exercicios = (document["exercicios"] as List<Map<String, Any>>).map { exMap ->
                    Exercicio(
                        id = exMap["id"] as String,
                        nome = exMap["nome"] as String,
                        grupoMuscular = exMap["grupoMuscular"] as String,
                        imagemUrl = exMap["imagemUrl"] as? String,
                        videoUrl = exMap["videoUrl"] as? String,
                        series = (exMap["series"] as List<Map<String, Any>>).map { serieMap ->
                            Serie(
                                numero = (serieMap["numero"] as Long).toInt(),
                                peso = serieMap["peso"] as Double,
                                repeticoes = (serieMap["repeticoes"] as Long).toInt()
                            )
                        }.toMutableList()
                    )
                }

                Treino(
                    id = (document["id"] as String).toString(),
                    alunoId = document["alunoId"] as String,
                    titulo = document["titulo"] as String,
                    diaDaSemana = document["diaDaSemana"] as String,
                    imagemTreino = document["imagemTreino"] as? String ?: "#E9F7FF",
                    exercicios = exercicios
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}