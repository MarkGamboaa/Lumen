package com.example.lumen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumen.databinding.ActivityEditNoteBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EditNote : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var currentNoteId: String? = null // Variable to store the ID of the note being edited

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras
        if (bundle != null) {
            currentNoteId = bundle.getString("note_id") // Assuming you pass "NOTE_ID"
            binding.editNoteTitle.setText(bundle.getString("note_title"))
            binding.editNoteContent.setText(bundle.getString("note_content"))
        }


        binding.saveNoteFAB.setOnClickListener {
            val title = binding.editNoteTitle.text.toString()
            val content = binding.editNoteContent.text.toString()
            // val userIdValue = auth.currentUser?.uid // Get current user's ID if needed

            if (currentNoteId == null) {
                Toast.makeText(this, "Error: Note ID is missing. Cannot update.", Toast.LENGTH_LONG).show()
                return@setOnClickListener // Don't proceed if there's no ID
            }

            val noteUpdateData = hashMapOf<String, Any>(
                "title" to title,
                "content" to content,
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("notes").document(currentNoteId!!) // Get document reference using its ID
                .update(noteUpdateData) // Call update on the DocumentReference
                .addOnSuccessListener {
                    Toast.makeText(this, "Note updated successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate back to MainActivity or NotesActivity
                    val intent = Intent(this, NotesActivity::class.java) // Assuming MainActivity shows the list
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clears back stack up to MainActivity
                    startActivity(intent)
                    finish() // Often better to just finish the EditNote activity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating note: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
