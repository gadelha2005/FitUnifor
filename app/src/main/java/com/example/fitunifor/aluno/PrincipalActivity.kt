package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fitunifor.R

class PrincipalActivity : AppCompatActivity() {

    // Variáveis para controlar o estado de participação nas aulas
    private var alunosYoga = 0
    private var alunosZumba = 0

    companion object {
        private const val CAPACIDADE_MAXIMA_YOGA = 20
        private const val CAPACIDADE_MAXIMA_ZUMBA = 22
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        // Inicializa os componentes
        val cardMeusTreinos = findViewById<CardView>(R.id.card_meus_treinos)
        val cardIa = findViewById<CardView>(R.id.card_ia)
        val cardCalendario = findViewById<CardView>(R.id.card_calendario)
        val buttonIniciarTreino = findViewById<Button>(R.id.button_iniciar_treino1)
        val btnParticiparYoga = findViewById<Button>(R.id.button_participar_aula_yoga)
        val btnParticiparZumba = findViewById<Button>(R.id.button_participar_aula_zumba)
        val textIntegrantesYoga = findViewById<TextView>(R.id.text_integrantes_alunos_yoga)
        val textIntegrantesZumba = findViewById<TextView>(R.id.text_integrantes_alunos_zumba)


        // Configura os listeners
        cardMeusTreinos.setOnClickListener { navigateToMeusTreinos() }
        cardCalendario.setOnClickListener { navigateCalendario() }
        cardIa.setOnClickListener { navigteIa() }
        buttonIniciarTreino.setOnClickListener { navigateToTreinoIniciado() }


        // Listener para o botão de Yoga
        btnParticiparYoga.setOnClickListener {
            if (btnParticiparYoga.text == "Participar") {
                // Tentativa de participar
                if (alunosYoga < CAPACIDADE_MAXIMA_YOGA) {
                    alunosYoga++
                    textIntegrantesYoga.text = "$alunosYoga alunos"
                    btnParticiparYoga.text = "Cancelar"
                    Toast.makeText(this, "Presença confirmada na aula de Yoga!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Aula de Yoga lotada!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Cancelar participação
                alunosYoga--
                textIntegrantesYoga.text = "$alunosYoga alunos"
                btnParticiparYoga.text = "Participar"
                Toast.makeText(this, "Participação cancelada!", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener para o botão de Zumba
        btnParticiparZumba.setOnClickListener {
            if (btnParticiparZumba.text == "Participar") {
                // Tentativa de participar
                if (alunosZumba < CAPACIDADE_MAXIMA_ZUMBA) {
                    alunosZumba++
                    textIntegrantesZumba.text = "$alunosZumba alunos"
                    btnParticiparZumba.text = "Cancelar"
                    Toast.makeText(this, "Presença confirmada na aula de Zumba!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Aula de Zumba lotada!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Cancelar participação
                alunosZumba--
                textIntegrantesZumba.text = "$alunosZumba alunos"
                btnParticiparZumba.text = "Participar"
                Toast.makeText(this, "Participação cancelada!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMeusTreinos() {
        try {
            val intent = Intent(this, MeusTreinosActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Não foi possível abrir Meus Treinos\n${e.localizedMessage}",
                Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun navigateToTreinoIniciado() {
        try {
            val intent = Intent(this, TreinoIniciadoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Não foi possível iniciar o treino\n${e.localizedMessage}",
                Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    private  fun navigteIa(){
        try {
            val intent = Intent(this, IAActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Não foi possível ir para a tela de ia\n${e.localizedMessage}",
                Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun navigateCalendario(){
        try {
            val intent = Intent(this, CalendarioActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            Toast.makeText(this,
                "Não foi possível ir para a tela de Calendario\n${e.localizedMessage}",
                Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


}