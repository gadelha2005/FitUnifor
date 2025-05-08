import Aula
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.fitunifor.R
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class AulasAdapterAluno(
    private var aulas: List<Aula>
) : RecyclerView.Adapter<AulasAdapterAluno.AulaAlunoViewHolder>() {

    // Inicialize o Firestore
    private val db = FirebaseFirestore.getInstance()

    // Método para atualizar as aulas
    fun atualizarAulas(novasAulas: List<Aula>) {
        aulas = novasAulas
        notifyDataSetChanged() // Notifica o RecyclerView para atualizar a lista
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaAlunoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aula_aluno, parent, false)
        return AulaAlunoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaAlunoViewHolder, position: Int) {
        val aula = aulas[position]

        holder.nomeAula.text = aula.nome
        holder.professor.text = "Prof ${aula.professor}"
        holder.horario.text = "Horário: ${aula.horario}"
        holder.capacidade.text = "${aula.maxAlunos} alunos"
        holder.textIntegrantes.text = "${aula.alunosMatriculados} alunos"

        // Botão participar
        holder.btnParticipar.text = if (aula.temVagas) {
            if (aula.alunosMatriculados > 0) "Cancelar" else "Participar"
        } else {
            "Sem vagas"
        }
        holder.btnParticipar.isEnabled = aula.temVagas || aula.alunosMatriculados > 0

        holder.btnParticipar.setOnClickListener {
            val aulaRef = db.collection("aulas").document(aula.id)

            if (aula.temVagas && holder.btnParticipar.text == "Participar") {
                // Incrementa alunosMatriculados
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(aulaRef)
                    val currentAlunos = snapshot.getLong("alunosMatriculados")?.toInt() ?: 0
                    transaction.update(aulaRef, "alunosMatriculados", currentAlunos + 1)
                }.addOnSuccessListener {
                    holder.btnParticipar.text = "Cancelar"
                    holder.textIntegrantes.text = "${aula.alunosMatriculados + 1} alunos"
                }.addOnFailureListener { e ->
                    Log.e("Participar", "Erro ao participar: ${e.message}", e)
                    Toast.makeText(holder.itemView.context, "Erro ao participar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else if (aula.alunosMatriculados > 0 && holder.btnParticipar.text == "Cancelar") {
                // Decrementa alunosMatriculados
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(aulaRef)
                    val currentAlunos = snapshot.getLong("alunosMatriculados")?.toInt() ?: 0
                    transaction.update(aulaRef, "alunosMatriculados", currentAlunos - 1)
                }.addOnSuccessListener {
                    holder.btnParticipar.text = "Participar"
                    holder.textIntegrantes.text = "${aula.alunosMatriculados - 1} alunos"
                }.addOnFailureListener { e ->
                    Log.e("Cancelar", "Erro ao cancelar: ${e.message}", e)
                    Toast.makeText(holder.itemView.context, "Erro ao cancelar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Quando não há vagas ou erro no estado do botão
                Toast.makeText(holder.itemView.context, "Não é possível participar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = aulas.size

    class AulaAlunoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeAula: TextView = itemView.findViewById(R.id.text_nome_aula)
        val professor: TextView = itemView.findViewById(R.id.text_professor)
        val horario: TextView = itemView.findViewById(R.id.text_horario)
        val capacidade: TextView = itemView.findViewById(R.id.text_capacidade)
        val textIntegrantes: TextView = itemView.findViewById(R.id.text_integrantes)
        val btnParticipar: Button = itemView.findViewById(R.id.button_participar_aula)
    }
}
