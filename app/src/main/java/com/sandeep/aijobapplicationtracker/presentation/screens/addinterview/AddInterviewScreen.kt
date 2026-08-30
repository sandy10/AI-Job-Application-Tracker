package com.sandeep.aijobapplicationtracker.presentation.screens.addinterview

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInterviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddInterviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onNavigateBack()
        } else if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Add Interview", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF191C1E),
                        modifier = Modifier.fillMaxWidth().padding(end = 48.dp),
                        textAlign = TextAlign.Center
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF464555))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AddInterviewContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onSaveClick = viewModel::saveInterview
            )
            
            // Bottom Sticky Area
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC).copy(alpha = 0.9f))
                    .border(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF4F46E5))
                        .clickable { viewModel.saveInterview("1", "Technical", "2026-09-01", "meet", "Interviewer") },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Save Interview", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun AddInterviewContent(
    modifier: Modifier = Modifier,
    uiState: UiState<Unit>,
    onSaveClick: (String, String, String, String, String) -> Unit
) {
    val scrollState = rememberScrollState()

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var meetingUrl by remember { mutableStateOf("") }
    var interviewer by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var remindMe by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        // Context Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFECEEF0))
                    .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for Google logo
                Box(modifier = Modifier.size(24.dp).background(Color.LightGray, CircleShape))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Senior Android Engineer", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                Text("Google", fontSize = 14.sp, color = Color(0xFF464555))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Interview Round", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
                Spacer(modifier = Modifier.height(4.dp))
                DropdownField(value = "1st Round")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Interview Type", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
                Spacer(modifier = Modifier.height(4.dp))
                DropdownField(value = "Technical")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Date", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
                Spacer(modifier = Modifier.height(4.dp))
                CustomTextField(value = date, onValueChange = { date = it }, placeholder = "YYYY-MM-DD")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Time", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
                Spacer(modifier = Modifier.height(4.dp))
                CustomTextField(value = time, onValueChange = { time = it }, placeholder = "HH:MM")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text("Meeting URL", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
            Spacer(modifier = Modifier.height(4.dp))
            CustomTextField(value = meetingUrl, onValueChange = { meetingUrl = it }, icon = Icons.Default.Share, placeholder = "https://meet.google.com/...")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text("Interviewer Name (Optional)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
            Spacer(modifier = Modifier.height(4.dp))
            CustomTextField(value = interviewer, onValueChange = { interviewer = it }, icon = Icons.Default.Person, placeholder = "e.g., Jane Doe")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Notes", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF464555))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE2DFFF).copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFF3525CD), RoundedCornerShape(12.dp)) // Emulated left border by just doing full border
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFF3525CD), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI summary available", fontSize = 10.sp, color = Color(0xFF3525CD))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            CustomTextField(value = notes, onValueChange = { notes = it }, placeholder = "Topics to prep, questions to ask...", singleLine = false, modifier = Modifier.height(80.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reminder Toggle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF3525CD), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Remind me before interview", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E))
                }
                Switch(
                    checked = remindMe,
                    onCheckedChange = { remindMe = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF4F46E5))
                )
            }
            if (remindMe) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(start = 36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("1 day before", fontSize = 14.sp, color = Color(0xFF191C1E))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF464555), modifier = Modifier.size(16.dp))
                }
                Box(modifier = Modifier.padding(start = 36.dp, top = 4.dp).height(1.dp).fillMaxWidth().background(Color(0xFFE2E8F0)))
            }
        }
    }
}

@Composable
private fun DropdownField(value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 14.sp, color = Color(0xFF191C1E))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF464555))
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    icon: ImageVector? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
    ) {
        Row(verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color(0xFF464555), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(placeholder, fontSize = 14.sp, color = Color(0xFF464555).copy(alpha = 0.5f))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF191C1E)),
                    cursorBrush = SolidColor(Color(0xFF3525CD)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
