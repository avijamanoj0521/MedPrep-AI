package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PersonalNoteEntity
import com.example.ui.theme.MedNavyPrimary
import com.example.ui.theme.MedTealAccent
import com.example.ui.theme.MedTealPrimary
import com.example.viewmodel.MedPrepViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: MedPrepViewModel) {
    val notes by viewModel.personalNotes.collectAsState()
    val enrolledSubjects by viewModel.enrolledSubjects.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedNoteForViewing by remember { mutableStateOf<PersonalNoteEntity?>(null) }

    val filteredNotes = notes.filter { note ->
        val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                note.content.contains(searchQuery, ignoreCase = true) ||
                note.subjectName.contains(searchQuery, ignoreCase = true)
        val matchesFav = if (showOnlyFavorites) note.isFavorite else true
        matchesSearch && matchesFav
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MedTealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_note_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Personal Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Surface(
                color = MedNavyPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Clinical Notes & Revision Summaries",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Organized by course, subject, and unit with high-yield points",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MedTealAccent,
                                fontSize = 11.sp
                            )
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { viewModel.navigateTo(Screen.AudioTranscribe) },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MedTealPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("notes_voice_dictation_button")
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice Dictate with gemini-3.5-transcribe"
                        )
                    }
                }
            }

            // Search & Favorites Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search personal notes & summaries...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("notes_search_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = { showOnlyFavorites = !showOnlyFavorites },
                    label = { Text("Starred") },
                    leadingIcon = {
                        Icon(
                            if (showOnlyFavorites) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (showOnlyFavorites) Color(0xFFF59E0B) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            // Notes List
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NoteAlt,
                            contentDescription = null,
                            tint = MedTealPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No notes found.", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Tap '+' to write a clinical lecture note or exam summary.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            onClick = { selectedNoteForViewing = note },
                            onToggleFav = { viewModel.toggleNoteFavorite(note) },
                            onDelete = { viewModel.deleteNote(note) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CreateNoteDialog(
            subjectNames = enrolledSubjects.map { it.name }.ifEmpty {
                listOf("Applied Anatomy & Physiology", "Nursing Foundation I", "Pharmacology", "Pathology")
            },
            onDismiss = { showAddDialog = false },
            onSave = { subject, unit, title, content, cat ->
                viewModel.savePersonalNote(subject, unit, title, content, cat)
                showAddDialog = false
            }
        )
    }

    selectedNoteForViewing?.let { note ->
        ViewNoteDetailsDialog(
            note = note,
            onDismiss = { selectedNoteForViewing = null },
            onSaveEdit = { viewModel.updatePersonalNote(it) }
        )
    }
}

@Composable
fun NoteCardItem(
    note: PersonalNoteEntity,
    onClick: () -> Unit,
    onToggleFav: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("note_card_${note.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MedNavyPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = note.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedNavyPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row {
                    IconButton(onClick = onToggleFav, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (note.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite Note",
                            tint = if (note.isFavorite) Color(0xFFF59E0B) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Note",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${note.subjectName} • ${note.unitName}",
                fontSize = 11.sp,
                color = MedTealPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun CreateNoteDialog(
    subjectNames: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf(subjectNames.firstOrNull() ?: "General Healthcare") }
    var unit by remember { mutableStateOf("Core Concepts") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("High-Yield Summary") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Clinical Study Note", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    placeholder = { Text("e.g. ECG Waves & Conduction Path") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_note_title")
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit / Topic") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content / Notes") },
                    placeholder = { Text("Write formulas, mechanisms, clinical signs...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("dialog_note_content")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(subject, unit, title, content, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary),
                modifier = Modifier.testTag("dialog_save_note_button")
            ) {
                Text("Save Note")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ViewNoteDetailsDialog(
    note: PersonalNoteEntity,
    onDismiss: () -> Unit,
    onSaveEdit: (PersonalNoteEntity) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(note.title) }
    var editedContent by remember { mutableStateOf(note.content) }
    var editedCategory by remember { mutableStateOf(note.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (!isEditing) {
                Column {
                    Text(note.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "${note.subjectName} • ${note.unitName} (${note.category})",
                        fontSize = 11.sp,
                        color = MedTealPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text("Edit Clinical Study Note", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (!isEditing) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = note.content,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedCategory,
                        onValueChange = { editedCategory = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedContent,
                        onValueChange = { editedContent = it },
                        label = { Text("Note Content") },
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                }
            }
        },
        confirmButton = {
            if (!isEditing) {
                Button(
                    onClick = { isEditing = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MedTealPrimary)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Note")
                }
            } else {
                Button(
                    onClick = {
                        onSaveEdit(note.copy(title = editedTitle, content = editedContent, category = editedCategory, updatedAtMillis = System.currentTimeMillis()))
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedNavyPrimary)
                ) {
                    Text("Save Changes")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
