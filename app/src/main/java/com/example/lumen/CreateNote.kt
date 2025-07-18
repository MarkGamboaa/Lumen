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
import com.google.firebase.Timestamp
import kotlin.random.Random // Import Random

// Option 1: Top-level object in this file
object ColorUtils {
    val PREDEFINED_CARD_COLOR_RES_IDS = listOf(
        R.color.note_bg_1,
        R.color.note_bg_2,
        R.color.note_bg_3,
        R.color.note_bg_4,
        R.color.note_bg_5,
        R.color.note_bg_6,
        R.color.note_bg_7,
        R.color.note_bg_8
    )
}

class CreateNote : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNoteBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // You can keep the createNewNoteWithPredefinedColor function separate
    // or integrate its logic directly into the setOnClickListener
    private fun createNoteData(title: String, content: String, userId: String): HashMap<String, Any?> {
        var chosenColorResId: Int? = null
        if (ColorUtils.PREDEFINED_CARD_COLOR_RES_IDS.isNotEmpty()) {
            chosenColorResId = ColorUtils.PREDEFINED_CARD_COLOR_RES_IDS[
                Random.nextInt(ColorUtils.PREDEFINED_CARD_COLOR_RES_IDS.size)
            ]
        }

        return hashMapOf(
            "user_id" to userId,
            "title" to title,
            "description" to "", // You can add a description EditText and replace this
            "content" to content,
            "timestamp" to FieldValue.serverTimestamp(), // Firestore server timestamp
            "colorResId" to chosenColorResId // Add the colorResId
        )
    }


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

            // Use the helper function to create the note data with color
            val noteData = createNoteData(title, content, userId)

            firestore.collection("notes")
                .add(noteData) // Save the HashMap
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
