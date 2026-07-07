package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StudyRepository(private val studyDao: StudyDao) {
    val allLectures: Flow<List<Lecture>> = studyDao.getAllLectures()
    val allRevisions: Flow<List<Revision>> = studyDao.getAllRevisions()

    fun getPendingRevisionsUpTo(date: Long): Flow<List<Revision>> {
        return studyDao.getPendingRevisionsUpTo(date)
    }

    suspend fun addLectureWithRevisions(subjectId: Int, title: String, dateMs: Long) {
        val lecture = Lecture(subjectId = subjectId, title = title, completedAt = dateMs)
        val lectureId = studyDao.insertLecture(lecture).toInt()
        
        val firstIntervalDays = 1
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMs
            add(Calendar.DAY_OF_YEAR, firstIntervalDays)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val firstRevision = Revision(
            lectureId = lectureId,
            scheduledDate = calendar.timeInMillis,
            isCompleted = false,
            intervalDays = firstIntervalDays
        )
        studyDao.insertRevisions(listOf(firstRevision))
    }

    suspend fun markRevisionCompleted(revision: Revision) {
        studyDao.updateRevision(revision.copy(isCompleted = true))
        
        val nextIntervals = mapOf(
            1 to 3,
            3 to 7,
            7 to 14,
            14 to 30
        )
        
        val nextIntervalDays = nextIntervals[revision.intervalDays]
        if (nextIntervalDays != null) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.DAY_OF_YEAR, nextIntervalDays)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            val nextRevision = Revision(
                lectureId = revision.lectureId,
                scheduledDate = calendar.timeInMillis,
                isCompleted = false,
                intervalDays = nextIntervalDays
            )
            studyDao.insertRevisions(listOf(nextRevision))
        }
    }
    
    suspend fun getLectureById(id: Int): Lecture? {
        return studyDao.getLectureById(id)
    }
}
