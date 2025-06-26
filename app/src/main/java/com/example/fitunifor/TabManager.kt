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
import com.example.fitunifor.LoginActivity
import com.example.fitunifor.aluno.PrincipalActivity
import com.example.fitunifor.EsqueciSenhaActivity
import com.example.fitunifor.MainActivity
import com.example.fitunifor.R
import com.example.fitunifor.administrador.PainelAdminstrativoActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TabManager constructor(
    private val context: Context,
    private val tabLayout: TabLayout,
    private val cardView: CardView
) : TabLayout.OnTabSelectedListener {

    private var currentTab: Int = 0
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
                    fazerLogin(email, senha)
                }
            }
        }

        view.findViewById<Button>(R.id.button_esqueci_senha)?.setOnClickListener {
            navigateToEsqueciSenha()
        }
    }

    private fun fazerLogin(email: String, senha: String) {
        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    redirecionarUsuario(email)
                } else {
                    showAlert("Falha no login", "Email ou senha incorretos. Tente novamente.")
                }
            }
    }

    private fun redirecionarUsuario(email: String) {
        val intent = if (email == "admin@fitunifor.com") {
            Intent(context, PainelAdminstrativoActivity::class.java)
        } else {
            Intent(context, PrincipalActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        (context as? AppCompatActivity)?.finish()
    }

    private fun setupCadastroView(view: View) {
        val editNomeCompleto = view.findViewById<EditText>(R.id.text_nome_completo)
        val editEmail = view.findViewById<EditText>(R.id.text_email)
        val editSenha = view.findViewById<EditText>(R.id.text_senha)
        val editConfirmarSenha = view.findViewById<EditText>(R.id.text_confirmar_senha)
        val buttonCadastrar = view.findViewById<Button>(R.id.button_cadastrar)

        buttonCadastrar.setOnClickListener {
            val nomeCompleto = editNomeCompleto.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()
            val confirmarSenha = editConfirmarSenha.text.toString().trim()

            // Limpa erros anteriores
            editNomeCompleto.error = null
            editEmail.error = null
            editSenha.error = null
            editConfirmarSenha.error = null

            when {
                nomeCompleto.isEmpty() -> {
                    editNomeCompleto.error = "Digite seu nome completo"
                    editNomeCompleto.requestFocus()
                    showAlert("Campo obrigatório", "Por favor, preencha o nome completo")
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
                confirmarSenha.isEmpty() -> {
                    editConfirmarSenha.error = "Confirme sua senha"
                    editConfirmarSenha.requestFocus()
                    showAlert("Campo obrigatório", "Por favor, confirme sua senha")
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    editEmail.error = "Email inválido"
                    editEmail.requestFocus()
                    showAlert("Email inválido", "Por favor, digite um email válido")
                }
                senha.length < 6 -> {
                    editSenha.error = "Senha deve ter pelo menos 6 caracteres"
                    editSenha.requestFocus()
                    showAlert("Senha fraca", "A senha deve ter no mínimo 6 caracteres")
                }
                senha != confirmarSenha -> {
                    editSenha.error = "As senhas não são iguais"
                    editConfirmarSenha.error = "As senhas não são iguais"
                    editSenha.requestFocus()
                    showAlert("Erro de senha", "As senhas não coincidem. Tente novamente.")
                }
                else -> {
                    cadastrarUsuario(nomeCompleto, email, senha)
                }
            }
        }
    }

    private fun cadastrarUsuario(nomeCompleto: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Cadastro no Authentication bem-sucedido, agora salva os dados no Firestore
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    salvarDadosUsuario(userId, nomeCompleto, email)
                } else {
                    showAlert("Erro no cadastro", task.exception?.message ?: "Erro desconhecido")
                }
            }
    }

    private fun salvarDadosUsuario(userId: String, nomeCompleto: String, email: String) {
        val usuario = hashMapOf(
            "nome" to nomeCompleto,
            "email" to email,
            "tipo" to "aluno" // Define o tipo de usuário como aluno por padrão
        )

        firestore.collection("usuarios")
            .document(userId)
            .set(usuario)
            .addOnSuccessListener {
                showAlert("Cadastro realizado", "Você foi cadastrado com sucesso!") {
                    // Após o cadastro, muda para a tab de login e preenche o email
                    tabLayout.getTabAt(0)?.select()
                    val loginView = cardView.findViewById<EditText>(R.id.text_email)
                    loginView?.setText(email)
                }
            }
            .addOnFailureListener { e ->
                showAlert("Erro ao salvar dados", e.message ?: "Erro desconhecido")
            }
    }

    private fun navigateToPainelAdministrativo() {
        val intent = Intent(context, PainelAdminstrativoActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        (context as? AppCompatActivity)?.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
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

    private fun showAlert(title: String, message: String, callback: (() -> Unit)? = null) {
        if (context is AppCompatActivity) {
            AlertDialog.Builder(context as AppCompatActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    callback?.invoke()
                }
                .create()
                .show()
        }
    }

    fun cleanup() {
        tabLayout.removeOnTabSelectedListener(this)
    }
}