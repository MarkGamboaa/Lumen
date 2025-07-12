package com.example.lumen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var btnGoToLogin: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignup = findViewById(R.id.btnSignup)
        btnGoToLogin = findViewById(R.id.btnGoToLogin)

        btnSignup.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val email = "$username@lumen.com"

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                || password.length < 6 || password != confirmPassword
            ) {
                Toast.makeText(this, "Fill all fields and ensure passwords match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if username is already taken
            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                    } else {
                        createFirebaseUser(email, password, username, firstName, lastName)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error checking username: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FirestoreError", "Error checking username", e)
                }
        }

        btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun createFirebaseUser(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val userData = mapOf(
                    "uid" to uid,
                    "username" to username,
                    "firstName" to firstName,
                    "lastName" to lastName
                )
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save user info", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
