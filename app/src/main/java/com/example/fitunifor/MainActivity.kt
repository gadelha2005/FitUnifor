package com.example.fitunifor

import TabManager
import com.example.fitunifor.R
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var tabManager: TabManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Correção: Use o método estático "setup" do companion object
        tabManager = TabManager.setup(
            activity = this,
            tabLayout = findViewById(R.id.tab_layout),
            cardView = findViewById(R.id.card_view))
    }

    override fun onDestroy() {
        super.onDestroy()
        tabManager.cleanup() // Limpeza para evitar vazamentos de memória
    }
}