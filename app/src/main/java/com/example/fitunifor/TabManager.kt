import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fitunifor.PrincipalActivity
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
        view.findViewById<View>(R.id.button_entrar)?.setOnClickListener {
            navigateToPrincipal()
        }
    }

    private fun setupCadastroView(view: View) {
        view.findViewById<View>(R.id.button_cadastrar)?.setOnClickListener {
            // Lógica específica do cadastro pode ser adicionada aqui
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

    fun cleanup() {
        tabLayout.removeOnTabSelectedListener(this)
    }
}