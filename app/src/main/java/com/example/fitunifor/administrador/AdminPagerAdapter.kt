package com.example.fitunifor.administrador

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fitunifor.administrador.aulas.AulasColetivasFragment

class AdminPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FichasTreinoFragment()
            1 -> AulasColetivasFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}