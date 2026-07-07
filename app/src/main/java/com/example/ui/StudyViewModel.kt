package com.example.ui

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.CalendarContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Lecture
import com.example.data.Revision
import com.example.data.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val color: Int
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = StudyRepository(database.studyDao())

    val allLectures: StateFlow<List<Lecture>> = repository.allLectures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRevisions: StateFlow<List<Revision>> = repository.allRevisions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    private val _currentDate = MutableStateFlow(getEndOfDayMillis(System.currentTimeMillis()))

    val pendingRevisionsToday = repository.getPendingRevisionsUpTo(getEndOfDayMillis(System.currentTimeMillis()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todayEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val todayEvents = _todayEvents.asStateFlow()

    fun fetchTodayEvents() {
        viewModelScope.launch {
            val events = withContext(Dispatchers.IO) {
                val eventsList = mutableListOf<CalendarEvent>()
                val projection = arrayOf(
                    CalendarContract.Events._ID,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.DISPLAY_COLOR
                )

                val startOfDay = getStartOfDayMillis(System.currentTimeMillis())
                val endOfDay = getEndOfDayMillis(System.currentTimeMillis())

                val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
                val selectionArgs = arrayOf(startOfDay.toString(), endOfDay.toString())

                try {
                    val cursor: Cursor? = getApplication<Application>().contentResolver.query(
                        CalendarContract.Events.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        "${CalendarContract.Events.DTSTART} ASC"
                    )

                    cursor?.use {
                        val idIndex = it.getColumnIndexOrThrow(CalendarContract.Events._ID)
                        val titleIndex = it.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
                        val dtStartIndex = it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
                        val dtEndIndex = it.getColumnIndexOrThrow(CalendarContract.Events.DTEND)
                        val colorIndex = it.getColumnIndexOrThrow(CalendarContract.Events.DISPLAY_COLOR)

                        while (it.moveToNext()) {
                            eventsList.add(
                                CalendarEvent(
                                    id = it.getLong(idIndex),
                                    title = it.getString(titleIndex) ?: "Untitled Event",
                                    startTime = it.getLong(dtStartIndex),
                                    endTime = it.getLong(dtEndIndex),
                                    color = it.getInt(colorIndex)
                                )
                            )
                        }
                    }
                } catch (e: SecurityException) {
                    // Permission not granted
                }
                eventsList
            }
            _todayEvents.value = events
        }
    }

    fun submitLecture(subjectId: Int, title: String, dateMs: Long) {
        viewModelScope.launch {
            repository.addLectureWithRevisions(subjectId, title, dateMs)
        }
    }

    fun completeRevision(revision: Revision) {
        viewModelScope.launch {
            repository.markRevisionCompleted(revision)
        }
    }
    
    suspend fun getLecture(id: Int): Lecture? {
        return repository.getLectureById(id)
    }

    private fun getStartOfDayMillis(timeMs: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getEndOfDayMillis(timeMs: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMs
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }
}
