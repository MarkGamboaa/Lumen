package com.example.lumen // Make sure this package matches where you want the file

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lumen.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NotesAdapter(
    private var notesList: List<Note>,
    private val onItemClicked: (Note) -> Unit // Lambda to handle item clicks
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // 2. ViewHolder Class
    class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        // Helper to format the timestamp
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(note: Note, clickListener: (Note) -> Unit) {
            binding.tvNoteTitle.text = note.title
            binding.tvNoteContentSnippet.text = note.getContentSnippet(80) // Display a snippet (e.g., 80 chars)
            binding.tvNoteTimestamp.text = note.timestamp?.toDate()?.let {
                dateFormat.format(it)
            } ?: "No date" // Handle null timestamp

            // Set the click listener for the entire item
            binding.root.setOnClickListener {
                clickListener(note)
            }
        }
    }

    // 3. onCreateViewHolder: Inflates the item layout and creates the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    // 4. onBindViewHolder: Binds data from a Note object to the ViewHolder's views
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.bind(currentNote, onItemClicked) // Pass the note and the click listener
    }

    // 5. getItemCount: Returns the total number of items in the list
    override fun getItemCount(): Int = notesList.size

    // 6. (Optional but Recommended) Method to update the adapter's data
    fun updateNotes(newNotes: List<Note>) {
        notesList = newNotes
        notifyDataSetChanged() // Basic refresh. Consider DiffUtil for better performance.
    }
}
