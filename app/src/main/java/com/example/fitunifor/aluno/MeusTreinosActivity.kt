package com.example.fitunifor.aluno

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.fitunifor.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MeusTreinosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_treinos)

        // Configurar clique na ImageView de voltar
        findViewById<ImageView>(R.id.icon_arrow_back_meus_treinos).setOnClickListener {
            navigateBackToPrincipal()
        }

        // Configurar ViewPager e TabLayout
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // Configurar adapter
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Conectar TabLayout com ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Meus Treinos"
                1 -> "Histórico"
                else -> ""
            }
        }.attach()
    }

    private fun navigateBackToPrincipal() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}