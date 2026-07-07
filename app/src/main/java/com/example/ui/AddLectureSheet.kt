package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLectureSheet(
    onDismiss: () -> Unit,
    onSubmit: (subjectId: Int, title: String, dateMs: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(Subject.PHYSICS) }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Complete a Lecture", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Lecture Topic / Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Subject", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Subject.entries.forEach { subject ->
                    FilterChip(
                        selected = selectedSubject == subject,
                        onClick = { selectedSubject = subject },
                        label = { Text(subject.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = subject.color.copy(alpha = 0.2f),
                            selectedLabelColor = subject.color
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(selectedSubject.id, title, System.currentTimeMillis())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = title.isNotBlank()
            ) {
                Text("Submit & Schedule Revisions")
            }
        }
    }
}
