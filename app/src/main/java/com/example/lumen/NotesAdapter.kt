package com.example.lumen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lumen.databinding.ItemNoteBinding


class NotesAdapter(
    private var notesList: List<Note>,
    private val onItemClicked: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // Default color resource if a note somehow doesn't have one
    private val defaultColorRes = R.color.note_bg_1 // Or any of your note_bg_x colors

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.tvNoteTitle.text = note.title
            val dateFormat = android.icu.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()) // Use fully qualified names
            binding.tvNoteTimestamp.text = note.timestamp?.toDate()?.let { date -> // 'date' is the non-null java.util.Date
                dateFormat.format(date)
            } ?: "No date" // Pr
            binding.tvNoteContentSnippet.text = note.getContentSnippet()

            val cardView = binding.root // This is your CardView

            note.colorResId?.let { colorResource ->
                // Use the color resource ID stored in the note object
                cardView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, colorResource))
            } ?: run {
                // No color resource ID stored (e.g., for older notes or if null), use a default
                cardView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, defaultColorRes))
            }

            binding.root.setOnClickListener { onItemClicked(note) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notesList[position])
    }

    override fun getItemCount(): Int = notesList.size

    fun updateNotes(newNotes: List<Note>) {
        notesList = newNotes
        // HIGHLY recommend using DiffUtil here
        notifyDataSetChanged()
    }
}
