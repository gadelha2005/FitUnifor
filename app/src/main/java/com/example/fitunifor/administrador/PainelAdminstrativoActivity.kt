package com.example.fitunifor.administrador

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.fitunifor.R
import com.example.fitunifor.aluno.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PainelAdminstrativoActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_painel_administrativo)

        // Configurar ViewPager e TabLayout
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // Configurar adapter
        val adapter = AdminPagerAdapter(this)
        viewPager.adapter = adapter

        // Conectar TabLayout com ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Fichas Treino"
                1 -> "Aulas Coletivas"
                else -> ""
            }
        }.attach()
    }
}