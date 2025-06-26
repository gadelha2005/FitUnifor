package com.example.fitunifor.aluno

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.fitunifor.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MeusTreinosActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_treinos)

        // Verificar se usuário está logado
        if (auth.currentUser == null) {
            finish()
            return
        }

        // Configurar clique na ImageView de voltar
        findViewById<ImageView>(R.id.icon_arrow_back_meus_treinos).setOnClickListener {
            navigateBackToPrincipal()
        }

        // Configurar ViewPager e TabLayout
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // Configurar adapter com os fragments
        val adapter = ViewPagerAdapter(this).apply {
            addFragment(MeusTreinosFragment(), "Meus Treinos")
            addFragment(HistoricoFragment(), "Histórico")
        }

        viewPager.adapter = adapter

        // Conectar TabLayout com ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        Log.d("MeusTreinosActivity", "Activity criada com sucesso")
    }

    private fun navigateBackToPrincipal() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MeusTreinosActivity::class.java)
    }
}