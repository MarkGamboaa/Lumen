package com.example.lumen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumen.databinding.ActivityNoteDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoteDetails : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNoteDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firebase initialization
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get note details from intent
        val bundle = intent.extras
        if (bundle != null) {
            noteId = bundle.getString("note_id")
            binding.createNoteTitle.text = bundle.getString("note_title")
            binding.createNoteContent.text = bundle.getString("note_content")
        }

        // Prepare bundle to send to EditNote
        val bundle2 = Bundle().apply {
            putString("note_id", noteId)
            putString("note_title", binding.createNoteTitle.text.toString())
            putString("note_content", binding.createNoteContent.text.toString())
        }

        // Edit note button
        binding.saveNoteFAB.setOnClickListener {
            val intent = Intent(this, EditNote::class.java)
            intent.putExtras(bundle2)
            startActivity(intent)
            finish()
        }

        // Delete note button
        binding.deleteNoteFAB.setOnClickListener {
            if (noteId.isNullOrBlank()) {
                Toast.makeText(this, "Note ID missing.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            confirmDelete()
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                noteId?.let { deleteNote(it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteNote(noteId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("notes")
            .document(noteId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val ownerId = document.getString("user_id")
                    if (ownerId == userId) {
                        // Proceed to delete
                        db.collection("notes")
                            .document(noteId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Note deleted successfully.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, NotesActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to delete note.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "You do not own this note.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Note not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading note info.", Toast.LENGTH_SHORT).show()
            }
    }
}
