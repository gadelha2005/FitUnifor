import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Aula(
    var id: String = UUID.randomUUID().toString(),
    val nome: String,
    val professor: String,
    val diaSemana: String,
    val horario: String,
    val maxAlunos: Int,
    val imagem: String = when (nome.lowercase()) {
        "yoga" -> "image_aula_yoga"
        "zumba" -> "image_aula_zumba"
        else -> "image_aula_coletiva"
    },
    var alunosMatriculados: Int = 0,
    var alunosMatriculadosList: List<String> = listOf()
) : Parcelable {
    constructor() : this("", "", "", "", "", 0, "", 0, listOf())

    fun temVagasDisponiveis(): Boolean {
        return alunosMatriculados < maxAlunos
    }

    fun usuarioJaMatriculado(usuarioId: String): Boolean {
        return alunosMatriculadosList.contains(usuarioId)
    }
}
