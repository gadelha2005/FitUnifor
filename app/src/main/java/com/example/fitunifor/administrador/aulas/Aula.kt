import android.os.Parcelable
import com.example.fitunifor.R
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Aula(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val professor: String,
    val diaSemana: String,
    val horario: String,
    val maxAlunos: Int,
    val imagem: String = "image_aula_coletiva", // Agora é uma String (nome/URL da imagem)
    var alunosMatriculados: Int = 0
) : Parcelable {

    constructor() : this("", "", "", "", "", 0, "", 0)
    val temVagas: Boolean get() = alunosMatriculados < maxAlunos
}