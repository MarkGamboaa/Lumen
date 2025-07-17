package com.example.lumen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumen.databinding.ActivityCreateNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CreateNote : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoteBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.saveNoteFAB.setOnClickListener {
            val title = binding.createNoteTitle.text.toString().trim()
            val content = binding.createNoteContent.text.toString().trim()
            val userId = auth.currentUser?.uid

            if (userId == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = hashMapOf(
                "user_id" to userId,
                "title" to title,
                "description" to "", // You can add a description EditText and replace this
                "content" to content,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("notes")
                .add(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
