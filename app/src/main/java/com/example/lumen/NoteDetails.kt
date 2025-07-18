package com.example.lumen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumen.databinding.ActivityNoteDetailsBinding

class NoteDetails : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailsBinding
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
        val bundle = intent.extras
        if (bundle != null) {
            binding.createNoteTitle.text = bundle.getString("note_title")
            binding.createNoteContent.text = bundle.getString("note_content")
        }

        val bundle2 = Bundle()
        bundle2.putString("note_id", bundle?.getString("note_id"))
        bundle2.putString("note_title", binding.createNoteTitle.text.toString())
        bundle2.putString("note_content", binding.createNoteContent.text.toString())

        binding.saveNoteFAB.setOnClickListener {
            val intent = Intent(this, EditNote::class.java)
            intent.putExtras(bundle2)
            startActivity(intent)
            finish()
        }
    }
}