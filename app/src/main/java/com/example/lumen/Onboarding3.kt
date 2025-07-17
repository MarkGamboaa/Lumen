package com.example.lumen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lumen.databinding.ActivityOnboarding3Binding

class Onboarding3 : AppCompatActivity() {
    private lateinit var binding: ActivityOnboarding3Binding
    private val PREFS_NAME = "MyPrefsFile"
    private val PREF_KEY_ONBOARDING_COMPLETE = "onboarding_complete"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboarding3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.nextButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean(PREF_KEY_ONBOARDING_COMPLETE, true)
                apply()
            }
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}