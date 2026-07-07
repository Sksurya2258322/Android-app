package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Lecture
import com.example.data.Revision
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarSheet(
    onDismiss: () -> Unit,
    allRevisions: List<Revision>,
    getLecture: suspend (Int) -> Lecture?
) {
    val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    val groupedRevisions = allRevisions
        .filter { !it.isCompleted }
        .groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.scheduledDate }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
        .toSortedMap()

    ModalBottomSheet(onDismissRequest = onDismiss, modifier = Modifier.fillMaxHeight(0.9f)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Text("Upcoming Calendar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (groupedRevisions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No upcoming revisions scheduled.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedRevisions.forEach { (dateMs, revisions) ->
                        item {
                            Text(
                                text = formatter.format(Date(dateMs)),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(revisions, key = { it.id }) { revision ->
                            var lecture by remember { mutableStateOf<Lecture?>(null) }
                            LaunchedEffect(revision.lectureId) {
                                lecture = getLecture(revision.lectureId)
                            }
                            if (lecture != null) {
                                val subject = Subject.fromId(lecture!!.subjectId)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(subject.color)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "${lecture!!.title} (Day ${revision.intervalDays})",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
