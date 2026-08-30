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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onNavigateToAddInterview: () -> Unit = {},
    onNavigateToResumeMatch: () -> Unit = {},
    viewModel: ApplicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF464555))
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
                        onClick = {},
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
                    Button(
                        onClick = {},
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
                }
            }
        }
    ) { paddingValues ->
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
                    onNavigateToAddInterview = onNavigateToAddInterview
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
    onNavigateToAddInterview: () -> Unit
) {
    val scrollState = rememberScrollState()

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
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "AI", tint = Color(0xFF4F46E5), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${data.matchScore}% Match", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4F46E5))
                    }
                }
            }
        }

        // Next Action Card
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
                    text = data.nextAction?.title ?: "Interview tomorrow at 10:00 AM",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF191C1E)
                )
                Text(
                    text = data.nextAction?.description ?: "AI recommends preparing Kotlin Coroutines, Jetpack Compose and System Design.",
                    fontSize = 14.sp,
                    color = Color(0xFF464555),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                Button(
                    onClick = { /* TODO */ },
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
            Text("Submitted Resume", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
                    .clickable { onNavigateToResumeMatch() }
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
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFDAD6)), // error-container
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "PDF", tint = Color(0xFFBA1A1A)) // error
                        }
                        Column {
                            Text(data.resumeUsed.ifEmpty { "Android_Senior_v4.pdf" }, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                            Text("✨ 86% Match", fontSize = 12.sp, color = Color(0xFF3525CD), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF3525CD).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("View Analysis", fontSize = 12.sp, color = Color(0xFF3525CD))
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onNavigateToAddInterview() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF3525CD), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Interview", fontSize = 12.sp, color = Color(0xFF3525CD))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.padding(start = 12.dp)) {
                // Done 1
                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 4.dp)) {
                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.White).border(2.dp, Color(0xFF3525CD), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = Color(0xFF3525CD), modifier = Modifier.size(12.dp))
                        }
                        Box(modifier = Modifier.width(1.dp).height(48.dp).background(Color(0xFFC7C4D8).copy(alpha = 0.5f)))
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text("Recruiter Screen", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                        Text("Completed Oct 15", fontSize = 14.sp, color = Color(0xFF777587))
                    }
                }
                // Done 2
                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 4.dp)) {
                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.White).border(2.dp, Color(0xFF3525CD), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = "Done", tint = Color(0xFF3525CD), modifier = Modifier.size(12.dp))
                        }
                        Box(modifier = Modifier.width(1.dp).height(48.dp).background(Color(0xFFC7C4D8).copy(alpha = 0.5f)))
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text("Technical Round 1", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                        Text("Completed Oct 22", fontSize = 14.sp, color = Color(0xFF777587))
                    }
                }
                // Upcoming
                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 4.dp)) {
                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFFFEF3C7)).border(2.dp, Color(0xFFD97706), CircleShape), contentAlignment = Alignment.Center) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFD97706)))
                        }
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.8f))
                                .border(1.dp, Color(0xFFC7C4D8))
                                .border(width = 4.dp, color = Color(0xFFD97706), shape = RoundedCornerShape(12.dp)) // Simulate left border
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("System Design", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                                Text("Tomorrow, 10:00 AM", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFFD97706))
                            }
                        }
                    }
                }
            }
        }

        // Recruiter
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
                            Text("Sarah Johnson", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                            Text("Technical Recruiter", fontSize = 14.sp, color = Color(0xFF777587))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF7F9FB))
                            .border(1.dp, Color(0xFFC7C4D8), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MailOutline, contentDescription = "Mail", tint = Color(0xFF464555), modifier = Modifier.size(20.dp))
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
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
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = data.notes.ifEmpty { "No notes added yet." },
                    fontSize = 14.sp,
                    color = Color(0xFF777587),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
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
