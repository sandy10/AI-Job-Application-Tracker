package com.sandeep.aijobapplicationtracker.presentation.screens.myresumes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.presentation.components.EmptyStateView
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyResumesScreen(
    onNavigateBack: () -> Unit,
    viewModel: MyResumesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF7F9FB),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "My Resumes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3525CD),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF464555))
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F9FB)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                "Your resume library",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191C1E),
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Keep different versions ready for different opportunities.",
                fontSize = 16.sp,
                color = Color(0xFF464555)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                when (val s = uiState) {
                    is UiState.Idle, is UiState.Loading -> LoadingView()
                    is UiState.Success -> {
                        if (s.data.isEmpty()) {
                            EmptyStateView(
                                title = "No Resumes",
                                subtitle = "Upload your first resume to get started."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(s.data) { resume ->
                                    ResumeCard(
                                        resume = resume,
                                        onSetPrimary = { viewModel.setPrimary(resume.id) }
                                    )
                                }
                            }
                        }
                    }
                    is UiState.Error -> EmptyStateView(
                        title = "Error",
                        subtitle = s.message
                    )
                    is UiState.Empty -> EmptyStateView(
                        title = "No Resumes",
                        subtitle = "Upload your first resume to get started."
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            UploadArea(onUploadClick = viewModel::uploadResume)
        }
    }
}

@Composable
private fun ResumeCard(
    resume: Resume,
    onSetPrimary: () -> Unit
) {
    val borderColor = if (resume.isPrimary) Color.Transparent else Color(0xFFC7C4D8)
    val cardModifier = if (resume.isPrimary) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(16.dp)) // Base border
            // Draw left primary border over it by overlaying a Box
    } else {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
    }

    Box(modifier = cardModifier.clickable { onSetPrimary() }) {
        if (resume.isPrimary) {
            Box(
                modifier = Modifier
                    .matchParentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .align(Alignment.CenterStart)
                        .background(Color(0xFF3525CD))
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconBg = if (resume.isPrimary) Color(0xFF3525CD).copy(alpha = 0.1f) else Color(0xFFECEEF0)
            val iconColor = if (resume.isPrimary) Color(0xFF3525CD) else Color(0xFF464555)
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resume.fileName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF191C1E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Updated ${resume.uploadedAt} • 1.2 MB",
                    fontSize = 14.sp,
                    color = Color(0xFF464555),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (resume.isPrimary) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF3525CD).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF3525CD).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Primary", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3525CD))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                IconButton(
                    onClick = { /* More options */ },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color(0xFF464555),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadArea(onUploadClick: () -> Unit) {
    val strokeColor = Color(0xFFC7C4D8)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F9FB).copy(alpha = 0.5f))
            .clickable { onUploadClick() }
            .drawBehind {
                drawRoundRect(
                    color = strokeColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                )
            }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFECEEF0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Upload",
                    tint = Color(0xFF464555),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Upload Resume",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF191C1E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "PDF • Maximum 5 MB",
                fontSize = 14.sp,
                color = Color(0xFF464555)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(1.dp, Color(0xFF3525CD), RoundedCornerShape(26.dp))
                    .clickable { onUploadClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color(0xFF3525CD),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Upload Resume",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF3525CD)
                    )
                }
            }
        }
    }
}
