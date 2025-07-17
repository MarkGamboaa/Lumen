package com.example.lumen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumen.databinding.ActivityFirstScreenBinding

class FirstScreen : AppCompatActivity() {
    private lateinit var binding: ActivityFirstScreenBinding
    private val PREFS_NAME = "MyPrefsFile"
    private val PREF_KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    private val SPLASH_DELAY_MS = 2000L // 2 seconds, adjust as needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Splash and Navigation Logic ---
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isOnboardingComplete = sharedPreferences.getBoolean(PREF_KEY_ONBOARDING_COMPLETE, false)

            if (isOnboardingComplete) {
                // Onboarding already completed, go to Login
                startActivity(Intent(this, Login::class.java))
            } else {
                startActivity(Intent(this, Onboarding1::class.java))
            }
            finish()
        }, SPLASH_DELAY_MS)
    }
}
