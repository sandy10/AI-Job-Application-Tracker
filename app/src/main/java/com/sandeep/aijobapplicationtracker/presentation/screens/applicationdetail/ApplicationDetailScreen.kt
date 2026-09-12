package com.sandeep.aijobapplicationtracker.presentation.screens.applicationdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.presentation.components.EmptyStateView
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddInterview: () -> Unit,
    onNavigateToResumeMatch: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    onNavigateToAiInterviewPrep: () -> Unit,
    onNavigateToEdit: () -> Unit = {},
    viewModel: ApplicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var topMenuExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var bottomMenuExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showDeleteDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF7F9FB), // bg-background
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF3525CD))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            type = "text/plain"
                            val role = if (uiState is UiState.Success) (uiState as UiState.Success).data.role else ""
                            val comp = if (uiState is UiState.Success) (uiState as UiState.Success).data.company else ""
                            val stat = if (uiState is UiState.Success) (uiState as UiState.Success).data.status else ""
                            putExtra(android.content.Intent.EXTRA_TEXT, "Check out this job application:\n\nRole: $role\nCompany: $comp\nStatus: $stat")
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Application"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF464555))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F9FB)
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .border(1.dp, Color(0xFFE2E8F0))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateToEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF191C1E)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7C4D8)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Edit Application", fontSize = 14.sp)
                    }
                    Box {
                        Button(
                            onClick = { bottomMenuExpanded = true },
                            modifier = Modifier
                                .size(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF464555)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7C4D8)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        androidx.compose.material3.DropdownMenu(
                            expanded = bottomMenuExpanded,
                            onDismissRequest = { bottomMenuExpanded = false }
                        ) {
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text("Delete Application") },
                                onClick = { 
                                    bottomMenuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (showDeleteDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Application") },
                text = { Text("Are you sure you want to delete this application?") },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteApplication()
                            onNavigateBack()
                        }
                    ) {
                        Text("Yes", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showDeleteDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = uiState) {
                is UiState.Idle, is UiState.Loading -> LoadingView()
                is UiState.Success -> ApplicationDetailContent(
                    data = s.data,
                    onNavigateToResumeMatch = onNavigateToResumeMatch,
                    onNavigateToAddInterview = onNavigateToAddInterview,
                    onNavigateToAssistant = onNavigateToAssistant,
                    onNavigateToAiInterviewPrep = onNavigateToAiInterviewPrep,
                    onAddNote = { note -> viewModel.updateNotes(note) }
                )
                is UiState.Empty -> EmptyStateView(
                    title = "No Data",
                    subtitle = "Application not found"
                )
                is UiState.Error -> EmptyStateView(
                    title = "Error",
                    subtitle = s.message.ifEmpty { "An unknown error occurred" }
                )
            }
        }
    }
}

@Composable
private fun ApplicationDetailContent(
    data: JobDetailData,
    onNavigateToResumeMatch: () -> Unit,
    onNavigateToAddInterview: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    onNavigateToAiInterviewPrep: () -> Unit,
    onAddNote: (String) -> Unit
) {
    var showNoteDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val isActionEnabled = data.status.name != "REJECTED" && data.status.name != "OFFER"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_brand_logo), // placeholder
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = data.role,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    )
                    Text(
                        text = data.company,
                        fontSize = 16.sp,
                        color = Color(0xFF464555),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color(0xFF777587), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${data.location} • Hybrid", fontSize = 14.sp, color = Color(0xFF777587))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEF3C7)) // amber-100
                        .border(1.dp, Color(0xFFD97706).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(data.status.name.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD97706)) // amber-600
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEEF2FF)) // indigo-50
                        .border(1.dp, Color(0xFF4F46E5).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable(enabled = isActionEnabled) { onNavigateToResumeMatch() }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "AI", tint = Color(0xFF4F46E5), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        val scoreText = when {
                            data.resumeUsed == "No Resume Uploaded" -> "Upload Resume to Match"
                            data.matchScore == 0 -> "Calculate Match"
                            else -> "${data.matchScore}% Match"
                        }
                        Text(scoreText, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4F46E5))
                    }
                }
            }
        }

        // Next Action Card
        if (data.nextAction != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEEF2FF))
                    .border(width = 1.dp, color = Color(0xFFEEF2FF), shape = RoundedCornerShape(12.dp))
                    .border(width = 4.dp, color = Color(0xFF4F46E5), shape = RoundedCornerShape(12.dp)) // Emulate left border by just using left padding hack or custom modifier, but let's just use standard border for now, or assume full border is ok based on compose limits without custom drawing. We will use a standard clip and border. Wait, to do left border:
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Fire", tint = Color(0xFF4F46E5), modifier = Modifier.size(24.dp)) // flame icon equivalent
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Next Action", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = data.nextAction.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF191C1E)
                    )
                    Text(
                        text = data.nextAction.description,
                        fontSize = 14.sp,
                        color = Color(0xFF464555),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    Button(
                        onClick = { onNavigateToAiInterviewPrep() },
                        enabled = isActionEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "AI", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Prepare with AI", fontSize = 14.sp)
                    }
                }
            }
        }

        // Details Grid
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    label = "Applied",
                    value = data.dateApplied
                )
                GlassCard(
                    modifier = Modifier.weight(1f),
                    label = "Source",
                    value = "LinkedIn"
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                label = "Job Type",
                value = "Full-time • Senior Level"
            )
        }

        // Submitted Resume
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Resume Analysis", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
                    .clickable(enabled = isActionEnabled) { 
                        onNavigateToResumeMatch()
                    }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "File", tint = Color(0xFF3525CD))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = data.resumeUsed, 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.SemiBold, 
                                color = Color(0xFF191C1E),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✨", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (data.resumeUsed == "No Resume Uploaded") "Update Resume to Match"
                                    else if (data.matchScore > 0) "${data.matchScore}% Match based on Job Description"
                                    else "Calculate Match",
                                    fontSize = 12.sp,
                                    color = Color(0xFF3525CD),
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF3525CD).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .clickable(enabled = isActionEnabled) { onNavigateToResumeMatch() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("View Analysis", fontSize = 12.sp, color = Color(0xFF3525CD), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Interviews Timeline
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Interviews", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(enabled = isActionEnabled) { 
                    onNavigateToAddInterview()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF3525CD), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Interview", fontSize = 12.sp, color = Color(0xFF3525CD))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (data.interviews.isEmpty()) {
                Text("No interviews scheduled yet.", fontSize = 14.sp, color = Color(0xFF777587))
            } else {
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    data.interviews.forEachIndexed { index, interview ->
                        Row {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 4.dp)) {
                                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFFFEF3C7)).border(2.dp, Color(0xFFD97706), CircleShape), contentAlignment = Alignment.Center) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFD97706)))
                                }
                                if (index < data.interviews.size - 1) {
                                    Box(modifier = Modifier.width(1.dp).height(48.dp).background(Color(0xFFC7C4D8).copy(alpha = 0.5f)))
                                }
                            }
                            Column(modifier = Modifier.padding(start = 16.dp, bottom = if (index < data.interviews.size - 1) 16.dp else 0.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.8f))
                                        .border(1.dp, Color(0xFFC7C4D8))
                                        .border(width = 4.dp, color = Color(0xFFD97706), shape = RoundedCornerShape(12.dp)) // Simulate left border
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                ) {
                                    Column {
                                        Text(interview.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                                        Text("${interview.type} • ${interview.date}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFFD97706))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recruiter
        if (data.recruiter.isNotBlank()) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Recruiter", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE6E8EA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Info, contentDescription = "Avatar", tint = Color(0xFF777587))
                            }
                            Column {
                                Text(data.recruiter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                            }
                        }
                    }
                }
            }
        }

        // Notes
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { 
                    showNoteDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF3525CD), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Note", fontSize = 12.sp, color = Color(0xFF3525CD))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F4F6))
                    .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                val notesStr = if (data.notes == "No notes.") "No notes added yet." else data.notes
                if (notesStr.startsWith("AI Extracted Skills:\n")) {
                    val parts = notesStr.split("\n\n", limit = 2)
                    val skillsLine = parts[0].removePrefix("AI Extracted Skills:\n")
                    val skillsList = skillsLine.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "AI", tint = Color(0xFF3525CD), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Extracted Skills", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3525CD))
                        }
                        
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            skillsList.forEach { skill ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFF3525CD).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(skill, fontSize = 12.sp, color = Color(0xFF464555))
                                }
                            }
                        }
                        
                        val userNotes = parts.getOrNull(1)
                        if (!userNotes.isNullOrBlank()) {
                            androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFC7C4D8).copy(alpha = 0.5f))
                            Text(
                                text = userNotes,
                                fontSize = 14.sp,
                                color = Color(0xFF191C1E),
                                lineHeight = 20.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = notesStr,
                        fontSize = 14.sp,
                        color = if (data.notes == "No notes.") Color(0xFF777587) else Color(0xFF191C1E),
                        fontStyle = if (data.notes == "No notes.") androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }

    if (showNoteDialog) {
        var noteText by androidx.compose.runtime.remember(data.notes) { androidx.compose.runtime.mutableStateOf(if (data.notes == "No notes.") "" else data.notes) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Add Note", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    label = { Text("Note content") },
                    maxLines = 5
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    onAddNote(noteText)
                    showNoteDialog = false
                }) {
                    Text("Save", color = Color(0xFF3525CD))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showNoteDialog = false }) {
                    Text("Cancel", color = Color(0xFF777587))
                }
            }
        )
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label.uppercase(), fontSize = 12.sp, color = Color(0xFF777587))
            Text(value, fontSize = 16.sp, color = Color(0xFF191C1E))
        }
    }
}
