package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLectureSheet(
    onDismiss: () -> Unit,
    onSubmit: (subjectId: Int, title: String, chapterName: String, dateMs: Long) -> Unit,
    sheetTitle: String = "Complete a Lecture",
    inputLabel: String = "Lecture Topic / Title",
    buttonText: String = "Submit & Schedule Revisions"
) {
    var title by remember { mutableStateOf("") }
    var chapterName by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(Subject.PHYSICS) }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(sheetTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = chapterName,
                onValueChange = { chapterName = it },
                label = { Text("Chapter Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(inputLabel) },
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
            
            val buttonBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF00F0FF), Color(0xFF7000FF))
            )
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(selectedSubject.id, title, chapterName, System.currentTimeMillis())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .then(
                        if (title.isNotBlank()) {
                            Modifier
                                .background(buttonBrush, shape = RoundedCornerShape(28.dp))
                                .border(1.dp, Color(0x4DFFFFFF), RoundedCornerShape(28.dp))
                        } else Modifier
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (title.isNotBlank()) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (title.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(28.dp),
                enabled = title.isNotBlank()
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
