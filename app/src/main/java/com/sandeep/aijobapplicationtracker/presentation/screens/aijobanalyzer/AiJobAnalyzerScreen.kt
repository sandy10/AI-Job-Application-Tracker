package com.sandeep.aijobapplicationtracker.presentation.screens.aijobanalyzer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiJobAnalyzerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit,
    viewModel: AiJobAnalyzerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            viewModel.resetState()
            onNavigateToResult()
        } else if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F9FB),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Analyze Job",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF191C1E))
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AiJobAnalyzerContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onAnalyzeClick = viewModel::analyzeJobDescription
        )
    }
}

@Composable
private fun AiJobAnalyzerContent(
    modifier: Modifier = Modifier,
    uiState: UiState<Unit>,
    onAnalyzeClick: (String) -> Unit
) {
    val isLoading = uiState is UiState.Loading
    var jobDescription by remember { mutableStateOf("") }
    val maxChars = 5000

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEEF2FF))
                .border(width = 1.dp, color = Color(0xFF06B6D4).copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp)) // Emulated left border
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = "AI", tint = Color(0xFF06B6D4), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI Powered Analysis", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3525CD))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Turn any job description into actionable insights",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Paste the complete job description below. AI will extract the important details for you.",
            fontSize = 14.sp,
            color = Color(0xFF464555)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Input Section
        Text("Job Description", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E), modifier = Modifier.padding(start = 4.dp))
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = jobDescription,
                onValueChange = { if (it.length <= maxChars) jobDescription = it },
                modifier = Modifier.fillMaxSize(),
                placeholder = { Text("Paste the job description here...", color = Color(0xFF777587), fontSize = 14.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFC7C4D8),
                    focusedBorderColor = Color(0xFF3525CD)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Text(
                text = "${jobDescription.length} / $maxChars",
                fontSize = 12.sp,
                color = if (jobDescription.length == maxChars) Color(0xFFBA1A1A) else Color(0xFF777587),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Privacy & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Privacy", tint = Color(0xFF464555), modifier = Modifier.size(16.dp)) // Using Warning or Shield equivalent
            Spacer(modifier = Modifier.width(8.dp))
            Text("Your job description is processed securely.", fontSize = 12.sp, color = Color(0xFF464555))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onAnalyzeClick(jobDescription) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4F46E5)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading && jobDescription.isNotBlank()
        ) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Analyzing...", fontSize = 16.sp)
            } else {
                Icon(Icons.Default.Star, contentDescription = "AI", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze with AI", fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "AI can make mistakes. Review extracted information before saving.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
