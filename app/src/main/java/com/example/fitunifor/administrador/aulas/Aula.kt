import android.os.Parcelable
import com.example.fitunifor.R
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
    val imagem: String = "image_aula_coletiva",
    var alunosMatriculados: Int = 0,
    var alunosMatriculadosList: List<String> = listOf() // Lista para armazenar os alunos matriculados
) : Parcelable {
    constructor() : this("", "", "", "", "", 0, "", 0, listOf())

    fun temVagasDisponiveis(): Boolean {
        return alunosMatriculados < maxAlunos
    }

    fun usuarioJaMatriculado(usuarioId: String): Boolean {
        return alunosMatriculadosList.contains(usuarioId)
    }
}
