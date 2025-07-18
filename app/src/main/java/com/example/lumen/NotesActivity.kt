package com.example.lumen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lumen.databinding.ActivityNotesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var notesAdapter: NotesAdapter

    private fun displayNotes(user_id: String) {
        firestore.collection("notes")
            .whereEqualTo("user_id", user_id) // Ensure your field name in Firestore is "userId"
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp, newest first
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    binding.rvNotes.visibility = View.GONE
                    binding.tvEmptyNotes.visibility = View.VISIBLE
                } else {
                    binding.rvNotes.visibility = View.VISIBLE
                    binding.tvEmptyNotes.visibility = View.GONE

                    val notesList = result.documents.mapNotNull { document ->
                        // Manually map to your Note data class
                        val noteData = document.data
                        if (noteData != null) {
                            Note(
                                id = document.id,
                                title = document.getString("title") ?: "No Title",
                                content = document.getString("content") ?: "",
                                timestamp = document.getTimestamp("timestamp"),
                                user_id = document.getString("user_id") ?: ""
                            )
                        } else {
                            null
                        }
                    }

                    if (::notesAdapter.isInitialized) {
                        notesAdapter.updateNotes(notesList)
                    } else {
                        notesAdapter = NotesAdapter(notesList) { clickedNote ->
                            // Handle note click, e.g., open detail activity
                            val bundle = Bundle()
                            bundle.putString("note_id", clickedNote.id)
                            bundle.putString("note_title", clickedNote.title)
                            bundle.putString("note_content", clickedNote.content)

                            val intent = Intent(this, NoteDetails::class.java) // Or your detail activity
                            intent.putExtras(bundle)
                            // You might want to pass other details or fetch fresh in the next activity
                            startActivity(intent)
                        }
                        binding.rvNotes.adapter = notesAdapter
                        binding.rvNotes.layoutManager = LinearLayoutManager(this)
                    }
                    Log.d("NotesActivity", "Notes loaded and adapter updated. Count: ${notesList.size}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("NotesActivity", "Error loading notes", e)
                Toast.makeText(this, "Error loading notes: ${e.message}", Toast.LENGTH_LONG).show()
                binding.rvNotes.visibility = View.GONE
                binding.tvEmptyNotes.visibility = View.VISIBLE
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets -> // Use binding.main
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.noteFAB.setOnClickListener {
            val intent = Intent(this, CreateNote::class.java)
            startActivity(intent)
        }
        binding.logoutButton.setOnClickListener {
            auth.signOut()

            // Show the Toast message
            Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()

            // Navigate to Login activity
            val intent = Intent(this, Login::class.java)
            // Optional: Clear the task stack so the user can't go back to NotesActivity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Finish NotesActivity
        }



        // Fetch notes when the activity is created (or resumed)
        auth.currentUser?.uid?.let { user_id ->
            displayNotes(user_id)
        } ?: run {
            // Handle case where user is not logged in, e.g., redirect to login
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            binding.rvNotes.visibility = View.GONE
            binding.tvEmptyNotes.visibility = View.VISIBLE
            binding.tvEmptyNotes.text = "Please log in to see your notes."
            // Optionally, navigate to LoginActivity
        }
        binding.aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        // Refresh notes when the activity resumes, in case new notes were added
        // or existing ones modified in another activity.
        auth.currentUser?.uid?.let { user_id ->
            displayNotes(user_id)
        }
    }
}
