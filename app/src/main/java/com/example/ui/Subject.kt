package com.example.ui

import androidx.compose.ui.graphics.Color

enum class Subject(val id: Int, val title: String, val color: Color) {
    PHYSICS(1, "Physics", Color(0xFF64B5F6)),
    CHEMISTRY(2, "Chemistry", Color(0xFF81C784)),
    ZOOLOGY(3, "Zoology", Color(0xFFE57373)),
    BOTANY(4, "Botany", Color(0xFFFFB74D));

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: PHYSICS
    }
}
