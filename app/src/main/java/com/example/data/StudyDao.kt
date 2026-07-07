package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: Lecture): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisions(revisions: List<Revision>)

    @Query("SELECT * FROM lectures ORDER BY completedAt DESC")
    fun getAllLectures(): Flow<List<Lecture>>

    @Query("SELECT * FROM revisions WHERE scheduledDate <= :date AND isCompleted = 0 ORDER BY scheduledDate ASC")
    fun getPendingRevisionsUpTo(date: Long): Flow<List<Revision>>

    @Query("SELECT * FROM revisions WHERE isCompleted = 1")
    fun getCompletedRevisions(): Flow<List<Revision>>

    @Update
    suspend fun updateRevision(revision: Revision)
    
    @Query("SELECT * FROM lectures WHERE id = :lectureId")
    suspend fun getLectureById(lectureId: Int): Lecture?
    
    @Query("SELECT * FROM revisions WHERE lectureId = :lectureId ORDER BY scheduledDate ASC")
    fun getRevisionsForLecture(lectureId: Int): Flow<List<Revision>>
    
    @Query("SELECT * FROM revisions ORDER BY scheduledDate ASC")
    fun getAllRevisions(): Flow<List<Revision>>
}
