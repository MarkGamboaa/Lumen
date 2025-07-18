package com.example.lumen

import com.google.firebase.Timestamp

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null,
    val user_id: String = "",
    var colorResId: Int? = null // Store the R.color resource ID
) {
    fun getContentSnippet(maxLength: Int = 100): String {
        return if (content.length > maxLength) {
            content.substring(0, maxLength).trim() + "..."
        } else {
            content
        }
    }
}