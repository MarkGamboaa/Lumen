package com.example.lumen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lumen.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val email = "$username@lumen.com"

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (password.length < 6 || password != confirmPassword) {
                Toast.makeText(this, "Ensure password is at least 6 characters and passwords match", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // Check if username already exists in Firestore
            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                    } else {
                        // Pass username, firstName, lastName to createFirebaseUser
                        createFirebaseUser(email, password, username, firstName, lastName)
                        // The success toast and navigation is now handled in createFirebaseUser's success listener
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error checking username: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnGoToLogin.setOnClickListener {
            // If user explicitly clicks "Go to Login" without signing up,
            // just finish this activity. LoginActivity is already on the back stack.
            finish()
        }
    }

    private fun createFirebaseUser(
        email: String,
        password: String,
        username: String, // Added username
        firstName: String, // Added firstName
        lastName: String // Added lastName
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult -> // Renamed from result to authResult for clarity
                val uid = authResult.user?.uid ?: return@addOnSuccessListener // Use authResult
                val userData = mapOf(
                    "uid" to uid,
                    "username" to username,
                    "firstName" to firstName,
                    "lastName" to lastName
                )
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener {
                        // ---- THIS IS THE KEY CHANGE for going back with a result ----
                        val resultIntent = Intent()
                        resultIntent.putExtra("SIGN_UP_SUCCESS", true) // Send a flag
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish() // This will now return to LoginActivity's onActivityResult
                        // Toast is now displayed in LoginActivity
                    }
                    .addOnFailureListener { e -> // Added e for exception
                        Toast.makeText(this, "Failed to save user info: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .addOnFailureListener { e -> // Added e for exception
                Toast.makeText(this, "Signup failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
