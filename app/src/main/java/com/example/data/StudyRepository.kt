package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StudyRepository(private val studyDao: StudyDao) {
    val allLectures: Flow<List<Lecture>> = studyDao.getAllLectures()
    val allRevisions: Flow<List<Revision>> = studyDao.getAllRevisions()

    fun getPendingRevisionsUpTo(date: Long): Flow<List<Revision>> {
        return studyDao.getPendingRevisionsUpTo(date)
    }

    suspend fun addLectureWithRevisions(subjectId: Int, title: String, chapterName: String, dateMs: Long) {
        val lecture = Lecture(subjectId = subjectId, title = title, chapterName = chapterName, completedAt = dateMs)
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
    
    suspend fun updateLecture(lecture: Lecture) {
        studyDao.updateLecture(lecture)
    }
    
    suspend fun deleteLecture(lectureId: Int) {
        studyDao.deleteRevisionsForLecture(lectureId)
        studyDao.deleteLecture(lectureId)
    }

    val allDpps: Flow<List<Dpp>> = studyDao.getAllDpps()
    
    val allCompletedCalendarEvents: Flow<List<CompletedCalendarEvent>> = studyDao.getAllCompletedCalendarEvents()

    suspend fun addDpp(subjectId: Int, title: String, chapterName: String, dateMs: Long) {
        val dpp = Dpp(subjectId = subjectId, title = title, chapterName = chapterName, completedAt = dateMs)
        studyDao.insertDpp(dpp)
    }
    
    suspend fun updateDpp(dpp: Dpp) {
        studyDao.updateDpp(dpp)
    }
    
    suspend fun deleteDpp(dppId: Int) {
        studyDao.deleteDpp(dppId)
    }
    
    suspend fun toggleCalendarEventCompletion(eventId: String, isCompleted: Boolean, dateMs: Long) {
        if (isCompleted) {
            studyDao.insertCompletedCalendarEvent(CompletedCalendarEvent(eventId, dateMs))
        } else {
            studyDao.deleteCompletedCalendarEvent(eventId)
        }
    }
    
    val allDailyReports: Flow<List<DailyReport>> = studyDao.getAllDailyReports()
    
    suspend fun saveDailyReport(dateMs: Long, completedTasks: Int, totalTasks: Int) {
        val report = DailyReport(dateMs, completedTasks, totalTasks)
        studyDao.insertDailyReport(report)
    }
}
