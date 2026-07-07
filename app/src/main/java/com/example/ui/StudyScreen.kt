package com.example.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Lecture
import com.example.data.Revision
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(viewModel: StudyViewModel = viewModel()) {
    val pendingRevisions by viewModel.pendingRevisionsToday.collectAsState()
    val allLectures by viewModel.allLectures.collectAsState()
    val allRevisions by viewModel.allRevisions.collectAsState()
    val todayEvents by viewModel.todayEvents.collectAsState()
    
    var showAddSheet by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.fetchTodayEvents()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_CALENDAR)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "StudySync",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        val dateString = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { showCalendar = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth, 
                            contentDescription = "Calendar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add completed lecture")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CompletionDashboard(allLectures, allRevisions)
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Today's Revisions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.inversePrimary)
                    ) {
                        Text(
                            "SPACED",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (pendingRevisions.isEmpty() && todayEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "You are all caught up for today! 🎉",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(pendingRevisions, key = { it.id }) { revision ->
                    var lecture by remember { mutableStateOf<Lecture?>(null) }
                    LaunchedEffect(revision.lectureId) {
                        lecture = viewModel.getLecture(revision.lectureId)
                    }
                    RevisionCard(
                        revision = revision,
                        lecture = lecture,
                        onComplete = { viewModel.completeRevision(revision) }
                    )
                }

                if (todayEvents.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Today's Calendar Events",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(todayEvents, key = { "event_${it.id}" }) { event ->
                        CalendarEventCard(event = event)
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddSheet) {
        AddLectureSheet(
            onDismiss = { showAddSheet = false },
            onSubmit = { subjectId, title, date ->
                viewModel.submitLecture(subjectId, title, date)
                showAddSheet = false
            }
        )
    }

    if (showCalendar) {
        CalendarSheet(
            onDismiss = { showCalendar = false },
            allRevisions = allRevisions,
            getLecture = { id -> viewModel.getLecture(id) }
        )
    }
}

@Composable
fun CompletionDashboard(lectures: List<Lecture>, revisions: List<Revision>) {
    val intervalToMastery = mapOf(1 to 1, 3 to 2, 7 to 3, 14 to 4, 30 to 5)
    
    var overallTotalMastery = 0
    var overallPossibleMastery = 0
    
    lectures.forEach { lecture ->
        val lectureRevisions = revisions.filter { it.lectureId == lecture.id && it.isCompleted }
        val maxInterval = lectureRevisions.maxOfOrNull { it.intervalDays } ?: 0
        overallTotalMastery += intervalToMastery[maxInterval] ?: 0
        overallPossibleMastery += 5
    }
    
    val progress = if (overallPossibleMastery == 0) 0f else overallTotalMastery.toFloat() / overallPossibleMastery
    val percentage = (progress * 100).toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "OVERALL MASTERY", 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$percentage%", 
                        style = MaterialTheme.typography.displayMedium, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.inversePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "$overallTotalMastery of $overallPossibleMastery mastery points",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Subject.entries.forEach { subject ->
                val subjectLectures = lectures.filter { it.subjectId == subject.id }
                var subMastery = 0
                var subPossible = 0
                
                subjectLectures.forEach { lecture ->
                    val lectureRevisions = revisions.filter { it.lectureId == lecture.id && it.isCompleted }
                    val maxInterval = lectureRevisions.maxOfOrNull { it.intervalDays } ?: 0
                    subMastery += intervalToMastery[maxInterval] ?: 0
                    subPossible += 5
                }
                
                val subProgressText = if (subPossible == 0) "0/0" else "$subMastery/$subPossible"
                val initials = subject.title.substring(0, 3).uppercase()
                
                Card(
                    modifier = Modifier.weight(1f).aspectRatio(0.9f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(subject.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = subject.color
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subProgressText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarEventCard(event: CalendarEvent) {
    val formatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val timeString = "${formatter.format(Date(event.startTime))} - ${formatter.format(Date(event.endTime))}"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RevisionCard(revision: Revision, lecture: Lecture?, onComplete: () -> Unit) {
    if (lecture == null) return
    val subject = Subject.fromId(lecture.subjectId)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(subject.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lecture.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${subject.title} • Day ${revision.intervalDays} Review",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onComplete) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "Mark as done",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
