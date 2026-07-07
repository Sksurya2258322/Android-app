package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lectures")
data class Lecture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int, // 1: Math, 2: Physics, 3: Chemistry, 4: Biology
    val title: String,
    val completedAt: Long
)

@Entity(tableName = "revisions")
data class Revision(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lectureId: Int,
    val scheduledDate: Long, // Start of the day for revision
    val isCompleted: Boolean = false,
    val intervalDays: Int // 1, 3, 7, 14, 30
)

data class LectureWithRevisions(
    val lecture: Lecture,
    val revisions: List<Revision>
)
