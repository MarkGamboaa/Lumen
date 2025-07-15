package com.example.lumen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.example.lumen.ui.dashboardFragment
import com.example.lumen.ui.homeFragment
import com.example.lumen.ui.profileFragment
import com.example.lumen.ui.settingsFragment

class Dashboard : AppCompatActivity() {
    private lateinit var navview: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.bottom_nav)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navview=findViewById(R.id.bottomNavigationView)

        navview.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigationHome -> replace(homeFragment())
                R.id.navigationDashboard -> replace(dashboardFragment())
                R.id.navigationSettings -> replace(settingsFragment())
                R.id.navigationProfile -> replace(profileFragment())
                else -> false
            }
            true
        }

    }

    private fun replace(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.navHost, fragment)
        fragmentTransaction.commit()
    }
}