package com.example.fitunifor.administrador

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.fitunifor.MainActivity
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

        // Pega o nome do usuário da intent
        var nomeUsuario = intent.getStringExtra("NOME_USUARIO")

        if (nomeUsuario.isNullOrEmpty()) {
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

        // Configura o botão de logout
        val logoutIcon = findViewById<ImageView>(R.id.icon_log_out)
        logoutIcon.setOnClickListener {
            exibirDialogLogout()
        }
    }

    private fun atualizarNome(nome: String?) {
        val nomeFormatado = nome?.replace("\"", "") ?: "Admin"
        findViewById<TextView>(R.id.text_nome_aluno).text = "Olá, $nomeFormatado"
    }

    private fun setupViewPager() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        viewPager.adapter = AdminPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Fichas Treino"
                1 -> "Aulas Coletivas"
                else -> ""
            }
        }.attach()
    }

    private fun exibirDialogLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sair da conta")
        builder.setMessage("Deseja realmente sair?")
        builder.setPositiveButton("Sim") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}
