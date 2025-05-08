package com.example.fitunifor.administrador

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.fitunifor.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PainelAdminstrativoActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_painel_administrativo)

        // Tenta obter o nome da intent primeiro
        var nomeUsuario = intent.getStringExtra("NOME_USUARIO")

        if (nomeUsuario.isNullOrEmpty()) {
            // Se não veio pela intent, busca do Firestore
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    nomeUsuario = document.getString("nome") ?: "Admin"
                    atualizarNome(nomeUsuario)
                }
                .addOnFailureListener { e ->
                    Log.e("PainelAdmin", "Erro ao buscar nome", e)
                    atualizarNome("Admin")
                }
        } else {
            atualizarNome(nomeUsuario)
        }

        setupViewPager()
    }

    private fun atualizarNome(nome: String?) {
        val nomeFormatado = nome?.replace("\"", "") ?: "Admin" // Remove aspas se existirem
        findViewById<TextView>(R.id.text_nome_aluno).text = "Olá, $nomeFormatado"
    }

    private fun setupViewPager() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        viewPager.adapter = AdminPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Fichas Treino"
                1 -> "Aulas Coletivas"
                else -> ""
            }
        }.attach()
    }
}