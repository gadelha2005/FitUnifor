import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fitunifor.aluno.PrincipalActivity
import com.example.fitunifor.EsqueciSenhaActivity
import com.google.android.material.tabs.TabLayout
import com.example.fitunifor.R

class TabManager constructor(
    private val context: Context,
    private val tabLayout: TabLayout,
    private val cardView: CardView
) : TabLayout.OnTabSelectedListener {

    private var currentTab: Int = 0

    companion object {
        fun setup(
            activity: AppCompatActivity,
            tabLayout: TabLayout,
            cardView: CardView
        ): TabManager {
            return TabManager(
                context = activity,
                tabLayout = tabLayout,
                cardView = cardView
            ).apply {
                tabLayout.addOnTabSelectedListener(this)
                showTab(0) // Mostra a primeira tab por padrão
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.position?.let { position ->
            currentTab = position
            showTab(position)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}

    fun showTab(position: Int) {
        when (position) {
            0 -> inflateContent(R.layout.activity_login) { setupLoginView(it) }
            1 -> inflateContent(R.layout.activity_cadastro) { setupCadastroView(it) }
            else -> throw IllegalArgumentException("Tab position inválida")
        }
        tabLayout.getTabAt(position)?.select()
    }

    private inline fun inflateContent(
        @LayoutRes layoutId: Int,
        crossinline setup: (View) -> Unit
    ) {
        cardView.removeAllViews()
        LayoutInflater.from(context).inflate(layoutId, cardView, true).apply {
            setup(this)
        }
    }

    private fun setupLoginView(view: View) {
        val buttonEntrar = view.findViewById<Button>(R.id.button_entrar)
        val editEmail = view.findViewById<EditText>(R.id.text_email)
        val editSenha = view.findViewById<EditText>(R.id.text_senha)

        buttonEntrar.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            // Limpa erros anteriores
            editEmail.error = null
            editSenha.error = null

            when {
                email.isEmpty() && senha.isEmpty() -> {
                    editEmail.error = "Digite seu email"
                    editSenha.error = "Digite sua senha"
                    editEmail.requestFocus()
                    showAlert("Campos obrigatórios", "Por favor, preencha o email e senha")
                }
                email.isEmpty() -> {
                    editEmail.error = "Digite seu email"
                    editEmail.requestFocus()
                    showAlert("Campo obrigatório", "Por favor, preencha o email")
                }
                senha.isEmpty() -> {
                    editSenha.error = "Digite sua senha"
                    editSenha.requestFocus()
                    showAlert("Campo obrigatório", "Por favor, preencha a senha")
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    editEmail.error = "Email inválido"
                    editEmail.requestFocus()
                    showAlert("Email inválido", "Por favor, digite um email válido")
                }
                else -> {
                    navigateToPrincipal()
                }
            }
        }

        view.findViewById<Button>(R.id.button_esqueci_senha)?.setOnClickListener {
            navigateToEsqueciSenha()
        }
    }

    private fun setupCadastroView(view: View) {
        view.findViewById<Button>(R.id.button_cadastrar)?.setOnClickListener {
            // Implemente a lógica de cadastro aqui quando necessário
        }
    }

    private fun navigateToPrincipal() {
        val intent = Intent(context, PrincipalActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        (context as? AppCompatActivity)?.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    private fun navigateToEsqueciSenha() {
        val intent = Intent(context, EsqueciSenhaActivity::class.java)
        context.startActivity(intent)
        (context as? AppCompatActivity)?.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    private fun showAlert(title: String, message: String) {
        if (context is AppCompatActivity) {
            AlertDialog.Builder(context as AppCompatActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    fun cleanup() {
        tabLayout.removeOnTabSelectedListener(this)
    }
}