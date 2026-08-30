package com.sandeep.aijobapplicationtracker.presentation.screens.addapplication

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddApplicationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAnalyzer: () -> Unit = {},
    viewModel: AddApplicationViewModel = hiltViewModel()
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
        containerColor = Color(0xFFF7F9FB), // bg-background
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Add Application",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF191C1E))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF7F9FB)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            viewModel.saveApplication("Company", "Title", "URL", "Remote", "Saved", "Notes")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5) // primary-container
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Save Application", fontSize = 16.sp, color = Color.White)
                    }
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Cancel", fontSize = 16.sp, color = Color(0xFF464555)) // on-surface-variant
                    }
                }
            }
        }
    ) { paddingValues ->
        AddApplicationContent(
            modifier = Modifier.padding(paddingValues),
            onAnalyzeClick = onNavigateToAnalyzer
        )
    }
}

@Composable
private fun AddApplicationContent(
    modifier: Modifier = Modifier,
    onAnalyzeClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    var company by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var jobUrl by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var workMode by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Saved for later") }
    var dateApplied by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var recruiter by remember { mutableStateOf("") }
    var noticePeriod by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // AI Analyzer Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEEF2FF))
                .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "AI", tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("Save time with AI", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                        Text("Paste a job description and let AI fill the details automatically.", fontSize = 14.sp, color = Color(0xFF464555))
                    }
                }
                
                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5)
                    ),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "AI", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Job Description", fontSize = 14.sp)
                }
            }
        }

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFC7C4D8))
            Text(
                text = "OR ENTER MANUALLY",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF777587),
                letterSpacing = 1.sp
            )
            Divider(modifier = Modifier.weight(1f), color = Color(0xFFC7C4D8))
        }

        // Form Fields
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Basic Details
            FormSection(title = "Basic Details") {
                FormInput(label = "Company Name *", value = company, onValueChange = { company = it }, placeholder = "e.g. Acme Corp")
                FormInput(label = "Job Title *", value = jobTitle, onValueChange = { jobTitle = it }, placeholder = "e.g. Senior Product Designer")
                FormInput(label = "Job URL", value = jobUrl, onValueChange = { jobUrl = it }, placeholder = "https://")
            }

            // Location & Logistics
            FormSection(title = "Location & Logistics") {
                FormInput(label = "Location", value = location, onValueChange = { location = it }, placeholder = "City, State or Country")
                FormInput(label = "Work Mode", value = workMode, onValueChange = { workMode = it }, placeholder = "Select mode")
                FormInput(label = "Source", value = source, onValueChange = { source = it }, placeholder = "Where did you find this?")
            }

            // Application Status & Details
            FormSection(title = "Application Status & Details") {
                FormInput(label = "Status", value = status, onValueChange = { status = it }, placeholder = "Saved for later")
                FormInput(label = "Date Applied", value = dateApplied, onValueChange = { dateApplied = it }, placeholder = "DD/MM/YYYY")
                FormInput(label = "Salary Range / CTC", value = salary, onValueChange = { salary = it }, placeholder = "e.g. $120k - $150k")
            }

            // Additional Context
            FormSection(title = "Additional Context") {
                FormInput(label = "Recruiter Info / Contact", value = recruiter, onValueChange = { recruiter = it }, placeholder = "Name or Email")
                FormInput(label = "Notice Period Required", value = noticePeriod, onValueChange = { noticePeriod = it }, placeholder = "e.g. 30 Days")
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Job Description", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E))
                    OutlinedTextField(
                        value = jobDescription,
                        onValueChange = { jobDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text("Paste full JD here...", color = Color(0xFF777587), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF2F4F6),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFC7C4D8),
                            focusedBorderColor = Color(0xFF3525CD)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Personal Notes", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        placeholder = { Text("Any thoughts or prep notes?", color = Color(0xFF777587), fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF2F4F6),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFC7C4D8),
                            focusedBorderColor = Color(0xFF3525CD)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF464555))
            content()
        }
    }
}

@Composable
private fun FormInput(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFF777587), fontSize = 14.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF2F4F6), // surface-container-low
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFC7C4D8), // outline-variant
                focusedBorderColor = Color(0xFF3525CD) // primary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}
