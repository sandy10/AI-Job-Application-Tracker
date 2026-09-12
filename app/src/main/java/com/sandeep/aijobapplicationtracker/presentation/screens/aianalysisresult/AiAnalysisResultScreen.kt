package com.sandeep.aijobapplicationtracker.presentation.screens.aianalysisresult

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sandeep.aijobapplicationtracker.domain.model.ExtractedJobData
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAnalysisResultScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AiAnalysisResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Assuming UiState.Empty indicates save success for MVP navigation
    LaunchedEffect(uiState) {
        if (uiState is UiState.Empty) {
            onNavigateToHome()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Analysis Complete",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3525CD), // primary
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF464555))
                    }
                },
                actions = {
                    Box(modifier = Modifier.size(48.dp)) // To center title
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
                    .background(Color(0xFFF7F9FB))
                    .border(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF3525CD)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3525CD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Edit Details", fontSize = 16.sp)
                    }
                    Button(
                        onClick = viewModel::confirmAndSave,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3525CD)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Save", modifier = Modifier.size(20.dp)) // placeholder for save icon
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Application", fontSize = 16.sp)
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
                is UiState.Success -> AiAnalysisResultContent(data = s.data)
                is UiState.Empty -> { /* Handled by LaunchedEffect */ }
                is UiState.Error -> { /* Show error */ }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiAnalysisResultContent(data: ExtractedJobData) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Status Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEEF2FF))
                .border(width = 1.dp, color = Color(0xFF3525CD).copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp)) // Emulated left border
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3525CD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Check", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text(data.role.ifEmpty { "Unknown Role" }, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                            Text(data.company.ifEmpty { "Unknown Company" }, fontSize = 14.sp, color = Color(0xFF464555))
                        }
                    }
                    Icon(Icons.Default.Star, contentDescription = "AI", tint = Color(0xFF00687A), modifier = Modifier.size(24.dp))
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Trend", tint = Color(0xFF3525CD), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Strong opportunity for your profile", fontSize = 14.sp, color = Color(0xFF3525CD))
                }
            }
        }

        // Extracted Information Section
        Column {
            Text("KEY DETAILS", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF464555), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailCard(
                        icon = Icons.Default.Star, // placeholder for work_history
                        label = "Experience",
                        value = data.experience.ifEmpty { "Not specified" },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailCard(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = data.location.ifEmpty { "Not specified" },
                        modifier = Modifier.weight(1f)
                    )
                    DetailCard(
                        icon = Icons.Default.Star, // fallback from Computer
                        label = "Seniority",
                        value = data.seniority.ifEmpty { "Not specified" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Required Skills
        val skillsList = if (data.skills.isNotEmpty()) data.skills else listOf("Kotlin", "Jetpack Compose", "Coroutines", "MVVM", "Firebase", "CI/CD")
        Column {
            Text("Required Skills", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF464555))
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skillsList.forEach { skill ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEEF2FF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(skill, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4F46E5))
                    }
                }
            }
        }

        // Important Keywords
        val keywordsList = listOf("Clean Architecture", "Performance Testing", "Leadership")
        Column {
            Text("Important Keywords", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF464555))
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                keywordsList.forEach { keyword ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(keyword, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
                    }
                }
            }
        }

        // Key Responsibilities
        Column {
            Text("Key Responsibilities", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF464555))
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val resps = listOf(
                        "Lead architecture discussions and decisions for new scalable mobile features.",
                        "Mentor junior developers and establish rigorous code review standards.",
                        "Collaborate closely with product and design teams to deliver high-quality UX."
                    )
                    resps.forEach { resp ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Arrow", tint = Color(0xFF3525CD), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(resp, fontSize = 14.sp, color = Color(0xFF191C1E))
                        }
                    }
                }
            }
        }

        // AI Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFCFFAFE))
                .border(width = 1.dp, color = Color(0xFF0891B2).copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "AI", tint = Color(0xFF0891B2), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Summary", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF164E63))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "\"This role strongly focuses on modern Android development, scalable architecture and technical ownership.\"",
                    fontSize = 14.sp,
                    color = Color(0xFF083344),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun DetailCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF2F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF00659A), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color(0xFF777587))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
        }
    }
}
