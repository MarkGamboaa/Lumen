package com.example.lumen // Or your actual package name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.lumen.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

const val SIGN_UP_REQUEST_CODE = 1001
const val SIGN_UP_SUCCESSFUL_RESULT_CODE = 1

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val signUpActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val signUpSuccess = result.data?.getBooleanExtra("SIGN_UP_SUCCESS", false) ?: false
                if (signUpSuccess) {
                    Toast.makeText(this, "Account Created! Please log in.", Toast.LENGTH_LONG).show()
                }
            }
            // You could also handle other result codes here if needed (e.g., SIGN_UP_CANCELLED)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fakeEmail = "$username@lumen.com"

            auth.signInWithEmailAndPassword(fakeEmail, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, NotesActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // When "Sign Up" button/text is clicked
        binding.btnRegister.setOnClickListener { // Assuming you have a button with id btnGoToSignUp
            val intent = Intent(this, SignUp::class.java)
            signUpActivityResultLauncher.launch(intent) // Use the new launcher
        }
    }
}
