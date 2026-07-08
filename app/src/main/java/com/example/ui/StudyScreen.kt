package com.example.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

@Composable
fun LiquidGlassBackground(content: @Composable () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) Color(0xFF0D0D12) else Color(0xFFF2F4F7)
    
    // Slow, soothing animated pulse for liquid glass orbs
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidGlass")
    val translationFactor by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbMovement"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
        // Glowing background orbs (mesh layout)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Orb 1: Sapphire Blue Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x331E88E5) else Color(0x221E88E5), 
                        Color.Transparent
                    ),
                    center = Offset(w * 0.15f + translationFactor, h * 0.25f - translationFactor),
                    radius = w * 0.8f
                ),
                center = Offset(w * 0.15f + translationFactor, h * 0.25f - translationFactor),
                radius = w * 0.8f
            )
            
            // Orb 2: Vibrant Violet / Magenta Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x2EFF007F) else Color(0x18FF007F), 
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f - translationFactor, h * 0.45f + translationFactor),
                    radius = w * 0.75f
                ),
                center = Offset(w * 0.85f - translationFactor, h * 0.45f + translationFactor),
                radius = w * 0.75f
            )
            
            // Orb 3: Soft Jade Green Glow (Botany/Zoology colors)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x2243A047) else Color(0x1543A047), 
                        Color.Transparent
                    ),
                    center = Offset(w * 0.35f + translationFactor, h * 0.8f + translationFactor),
                    radius = w * 0.85f
                ),
                center = Offset(w * 0.35f + translationFactor, h * 0.8f + translationFactor),
                radius = w * 0.85f
            )
        }
        
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassyCard(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    glowColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    // Apple-style translucent glass backing colors
    val baseGlassColor = if (isDark) {
        Color(0x1C1C1C24)
    } else {
        Color(0x75FFFFFF)
    }
    
    // Apple-style glossy white specular edge stroke
    val defaultBorderColor = if (isDark) {
        Color(0x28FFFFFF)
    } else {
        Color(0x4DFFFFFF)
    }
    
    val actualBorderColor = borderColor ?: defaultBorderColor
    
    val baseModifier = modifier
        .shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(24.dp),
            clip = false,
            ambientColor = Color(0x0A000000),
            spotColor = Color(0x14000000)
        )
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    baseGlassColor,
                    baseGlassColor.copy(alpha = baseGlassColor.alpha * 0.8f)
                )
            ),
            shape = RoundedCornerShape(24.dp)
        )
        .border(1.2.dp, actualBorderColor, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))

    val finalModifier = if (onClick != null || onLongClick != null) {
        baseModifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = { onLongClick?.invoke() }
        )
    } else {
        baseModifier
    }

    Box(modifier = finalModifier) {
        // Specular glow element inside the glass card
        if (glowColor != null) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.5f
                    ),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.5f
                )
            }
        }
        
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun LiquidProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    
    // Liquid neon gradient
    val progressBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF00F0FF), // Neon Cyan
            Color(0xFF7000FF), // Bright Violet
            Color(0xFFFF007F)  // Hot Magenta
        )
    )
    
    val trackBgColor = if (isDark) Color(0x33000000) else Color(0x14000000)
    val trackBorderColor = if (isDark) Color(0x1FFFFFFF) else Color(0x1A000000)

    Box(
        modifier = modifier
            .background(trackBgColor, shape = RoundedCornerShape(50))
            .border(1.dp, trackBorderColor, shape = RoundedCornerShape(50))
            .padding(2.5.dp)
    ) {
        if (progress > 0.02f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .background(progressBrush, shape = RoundedCornerShape(50))
            ) {
                // Glass glossy highlight overlay on top of progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x99FFFFFF), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(viewModel: StudyViewModel = viewModel()) {
    val pendingRevisions by viewModel.pendingRevisionsToday.collectAsState()
    val allLectures by viewModel.allLectures.collectAsState()
    val allRevisions by viewModel.allRevisions.collectAsState()
    val allDpps by viewModel.allDpps.collectAsState()
    val todayEvents by viewModel.todayEvents.collectAsState()
    val completedCalendarEvents by viewModel.completedCalendarEvents.collectAsState()
    
    val todayStart = remember { Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis }
    val todayEnd = todayStart + 24 * 60 * 60 * 1000 - 1

    val revisionsToday = allRevisions.filter { it.scheduledDate in todayStart..todayEnd }
    val completedRevisionsToday = revisionsToday.count { it.isCompleted }
    val totalRevisionsToday = revisionsToday.size
    
    val completedEventsToday = todayEvents.count { event -> completedCalendarEvents.any { it.eventId == event.id.toString() } }
    val totalEventsToday = todayEvents.size
    
    val todayTasksCompleted = completedRevisionsToday + completedEventsToday
    val todayTasksTotal = totalRevisionsToday + totalEventsToday
    
    var showAddSheet by remember { mutableStateOf(false) }
    var showAddDppSheet by remember { mutableStateOf(false) }
    var showDppList by remember { mutableStateOf(false) }
    var showDailyReports by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    
    var lectureMenuId by remember { mutableStateOf<Int?>(null) }
    var lectureToEdit by remember { mutableStateOf<Lecture?>(null) }
    var lectureToDelete by remember { mutableStateOf<Lecture?>(null) }

    var dppMenuId by remember { mutableStateOf<Int?>(null) }
    var dppToEdit by remember { mutableStateOf<com.example.data.Dpp?>(null) }
    var dppToDelete by remember { mutableStateOf<com.example.data.Dpp?>(null) }

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

    LiquidGlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "StudySync",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            val dateString = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
                            Text(
                                text = dateString,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                        
                        // Glassmorphic Calendar Button
                        IconButton(
                            onClick = { showCalendar = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x66FFFFFF)
                                )
                                .border(
                                    1.dp, 
                                    if (isSystemInDarkTheme()) Color(0x22FFFFFF) else Color(0x4DFFFFFF), 
                                    RoundedCornerShape(14.dp)
                                )
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth, 
                                contentDescription = "Calendar",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showAddSheet = true },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00F0FF),
                                    Color(0xFF7000FF)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(1.2.dp, Color(0x66FFFFFF), RoundedCornerShape(24.dp))
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete Lecture", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CompletionDashboard(
                        lectures = allLectures, 
                        revisions = allRevisions,
                        selectedSubjectId = selectedSubjectId,
                        onSubjectClick = { clickedId -> 
                            selectedSubjectId = clickedId
                        },
                        dppCount = allDpps.size,
                        onDppClick = { showDppList = true },
                        onAddDppClick = { showAddDppSheet = true },
                        todayTasksCompleted = todayTasksCompleted,
                        todayTasksTotal = todayTasksTotal,
                        onDailyReportsClick = { showDailyReports = true },
                        onSaveDailyReport = { viewModel.saveDailyReport(todayTasksCompleted, todayTasksTotal) }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Today's Schedule",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Surface(
                            color = Color(0x227000FF),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x407000FF))
                        ) {
                            Text(
                                "LEITNER SPACED",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9150FF)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                if (pendingRevisions.isEmpty() && todayEvents.isEmpty()) {
                    item {
                        GlassyCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "You are all caught up for today! 🎉",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        items(todayEvents, key = { "event_${it.id}" }) { event ->
                            val isCompleted = completedCalendarEvents.any { it.eventId == event.id.toString() }
                            CalendarEventCard(
                                event = event,
                                isCompleted = isCompleted,
                                onToggleComplete = {
                                    viewModel.toggleCalendarEventCompletion(event.id.toString(), !isCompleted, System.currentTimeMillis())
                                }
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(96.dp)) }
            }
        }
    }

    if (showAddSheet) {
        AddLectureSheet(
            onDismiss = { showAddSheet = false },
            onSubmit = { subjectId, title, chapterName, date ->
                viewModel.submitLecture(subjectId, title, chapterName, date)
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
    
    lectureToEdit?.let { lecture ->
        EditLectureDialog(
            lecture = lecture,
            onDismiss = { lectureToEdit = null },
            onSave = { newTitle ->
                viewModel.updateLecture(lecture.copy(title = newTitle))
                lectureToEdit = null
            }
        )
    }

    lectureToDelete?.let { lecture ->
        AlertDialog(
            onDismissRequest = { lectureToDelete = null },
            title = { Text("Delete Lecture") },
            text = { Text("Are you sure you want to delete '${lecture.title}' and all its scheduled revisions? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLecture(lecture.id)
                        lectureToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { lectureToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showAddDppSheet) {
        AddLectureSheet(
            onDismiss = { showAddDppSheet = false },
            onSubmit = { subjectId, title, chapterName, date ->
                viewModel.submitDpp(subjectId, title, chapterName, date)
                showAddDppSheet = false
            },
            sheetTitle = "Complete a DPP",
            inputLabel = "DPP Topic / Title",
            buttonText = "Save DPP"
        )
    }

    if (showDailyReports) {
        val dailyReports by viewModel.allDailyReports.collectAsState()
        
        ModalBottomSheet(
            onDismissRequest = { showDailyReports = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Daily Reports",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (dailyReports.isEmpty()) {
                    Text(
                        "No daily reports saved yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dailyReports, key = { "report_${it.dateMs}" }) { report ->
                            val formatter = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
                            val dateString = formatter.format(Date(report.dateMs))
                            val percentage = if (report.totalTasks > 0) (report.completedTasks.toFloat() / report.totalTasks * 100).toInt() else 0
                            
                            GlassyCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = dateString,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${report.completedTasks} / ${report.totalTasks} completed",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                    Text(
                                        text = "$percentage%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDppList) {
        ModalBottomSheet(
            onDismissRequest = { showDppList = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Completed DPPs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dppsByDate = allDpps.groupBy {
                        val cal = Calendar.getInstance().apply { 
                            timeInMillis = it.completedAt
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0) 
                        }
                        cal.timeInMillis
                    }.toSortedMap(compareByDescending { it })
                    
                    dppsByDate.forEach { (dateMs, dpps) ->
                        item {
                            val formatter = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
                            val dateString = formatter.format(Date(dateMs))
                            Text(
                                text = dateString, 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.Bold, 
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(dpps, key = { "dpp_${it.id}" }) { dpp ->
                            GlassyCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = Subject.fromId(dpp.subjectId).color.copy(alpha = 0.3f),
                                contentPadding = PaddingValues(16.dp),
                                onLongClick = { dppMenuId = dpp.id }
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Column {
                                        Text(
                                            text = if (dpp.chapterName.isNotBlank()) "${dpp.chapterName} - ${dpp.title}" else dpp.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = Subject.fromId(dpp.subjectId).title,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                    
                                    DropdownMenu(
                                        expanded = dppMenuId == dpp.id,
                                        onDismissRequest = { dppMenuId = null }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                dppMenuId = null
                                                dppToEdit = dpp
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Delete") },
                                            onClick = {
                                                dppMenuId = null
                                                dppToDelete = dpp
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    selectedSubjectId?.let { subjectId ->
        ModalBottomSheet(
            onDismissRequest = { selectedSubjectId = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            val subject = Subject.fromId(subjectId)
            val subjectLectures = allLectures.filter { it.subjectId == subjectId }.sortedByDescending { it.completedAt }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Completed Lectures - ${subject.title}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = subject.color
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (subjectLectures.isEmpty()) {
                    Text(
                        "No lectures completed yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val lecturesByDate = subjectLectures.groupBy {
                            val cal = Calendar.getInstance().apply { 
                                timeInMillis = it.completedAt
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0) 
                            }
                            cal.timeInMillis
                        }.toSortedMap(compareByDescending { it })
                        
                        lecturesByDate.forEach { (dateMs, lectures) ->
                            item {
                                val formatter = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
                                val dateString = formatter.format(Date(dateMs))
                                Text(
                                    text = dateString, 
                                    style = MaterialTheme.typography.titleMedium, 
                                    fontWeight = FontWeight.Bold, 
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                                    color = subject.color
                                )
                            }
                            items(lectures, key = { "completed_${it.id}" }) { lecture ->
                                GlassyCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    borderColor = subject.color.copy(alpha = 0.3f),
                                    contentPadding = PaddingValues(16.dp),
                                    onLongClick = { lectureMenuId = lecture.id }
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Column {
                                            Text(
                                                text = if (lecture.chapterName.isNotBlank()) "${lecture.chapterName} - ${lecture.title}" else lecture.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Completed Lecture",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                        }
                                        
                                        DropdownMenu(
                                            expanded = lectureMenuId == lecture.id,
                                            onDismissRequest = { lectureMenuId = null }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Edit") },
                                                onClick = {
                                                    lectureMenuId = null
                                                    lectureToEdit = lecture
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Delete") },
                                                onClick = {
                                                    lectureMenuId = null
                                                    lectureToDelete = lecture
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    dppToEdit?.let { dpp ->
        EditDppDialog(
            dpp = dpp,
            onDismiss = { dppToEdit = null },
            onSave = { newTitle ->
                viewModel.updateDpp(dpp.copy(title = newTitle))
                dppToEdit = null
            }
        )
    }

    dppToDelete?.let { dpp ->
        AlertDialog(
            onDismissRequest = { dppToDelete = null },
            title = { Text("Delete DPP") },
            text = { Text("Are you sure you want to delete '${dpp.title}'? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDpp(dpp.id)
                        dppToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { dppToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EditDppDialog(
    dpp: com.example.data.Dpp,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var title by remember { mutableStateOf(dpp.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit DPP") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("DPP Topic / Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditLectureDialog(
    lecture: Lecture,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var title by remember { mutableStateOf(lecture.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Lecture") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Lecture Topic / Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CompletionDashboard(
    lectures: List<Lecture>,
    revisions: List<Revision>,
    selectedSubjectId: Int?,
    onSubjectClick: (Int) -> Unit,
    dppCount: Int = 0,
    onDppClick: () -> Unit = {},
    onAddDppClick: () -> Unit = {},
    todayTasksCompleted: Int = 0,
    todayTasksTotal: Int = 0,
    onDailyReportsClick: () -> Unit = {},
    onSaveDailyReport: () -> Unit = {}
) {
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
        GlassyCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            glowColor = Color(0x1200F0FF)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "OVERALL MASTERY", 
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$percentage%", 
                        style = MaterialTheme.typography.headlineMedium, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    LiquidProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "$overallTotalMastery of $overallPossibleMastery mastery points",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        if (todayTasksTotal > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            
            val percentage = if (todayTasksTotal > 0) (todayTasksCompleted.toFloat() / todayTasksTotal * 100).toInt() else 0
            
            GlassyCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                contentPadding = PaddingValues(16.dp),
                onClick = onDailyReportsClick
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "TODAY's REPORT",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$todayTasksCompleted out of $todayTasksTotal events completed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Completion: $percentage%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onSaveDailyReport) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Save Report",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
                
                val isSelected = selectedSubjectId == subject.id
                val currentBorderColor = if (isSelected) subject.color else subject.color.copy(alpha = 0.35f)
                val currentGlowColor = if (isSelected) subject.color.copy(alpha = 0.3f) else subject.color.copy(alpha = 0.12f)
                
                GlassyCard(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.75f),
                    borderColor = currentBorderColor,
                    glowColor = currentGlowColor,
                    contentPadding = PaddingValues(8.dp),
                    onClick = { onSubjectClick(subject.id) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(subject.color.copy(alpha = 0.25f), subject.color.copy(alpha = 0.02f))
                                    )
                                )
                                .border(1.dp, subject.color.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val lightBlueColor = Color(0xFF81D4FA)
        
        GlassyCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = lightBlueColor.copy(alpha = 0.5f), 
            glowColor = lightBlueColor.copy(alpha = 0.1f), 
            onClick = onDppClick
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "COMPLETED DPP",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = lightBlueColor,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$dppCount DPPs solved",
                        style = MaterialTheme.typography.bodySmall,
                        color = lightBlueColor
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, lightBlueColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    IconButton(onClick = onAddDppClick) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add DPP",
                            tint = lightBlueColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarEventCard(event: CalendarEvent, isCompleted: Boolean, onToggleComplete: () -> Unit) {
    val formatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val timeString = "${formatter.format(Date(event.startTime))} - ${formatter.format(Date(event.endTime))}"
    
    val currentBorderColor = if (isCompleted) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
    
    GlassyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        borderColor = currentBorderColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onToggleComplete) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Toggle completion",
                    tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun RevisionCard(revision: Revision, lecture: Lecture?, onComplete: () -> Unit) {
    if (lecture == null) return
    val subject = Subject.fromId(lecture.subjectId)
    
    GlassyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        borderColor = subject.color.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                subject.color.copy(alpha = 0.25f),
                                subject.color.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(1.dp, subject.color.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle, 
                    contentDescription = null, 
                    tint = subject.color, 
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lecture.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${subject.title} • Day ${revision.intervalDays} Review",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onComplete) {
                Icon(
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = "Mark as done",
                    tint = subject.color.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
