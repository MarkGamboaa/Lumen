package com.example.lumen

import com.google.firebase.Timestamp

data class Note(
    val id: String = "", // Document ID
    val title: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null, // Firebase Timestamp
    val user_id: String = ""
) {
    fun getContentSnippet(maxLength: Int = 100): String {
        return if (content.length > maxLength) {
            content.substring(0, maxLength).trim() + "..."
        } else {
            content
        }
    }
}