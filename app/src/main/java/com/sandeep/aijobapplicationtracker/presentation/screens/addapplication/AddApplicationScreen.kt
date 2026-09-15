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
import androidx.compose.material.icons.filled.DateRange
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
    jobId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToAnalyzer: () -> Unit = {},
    viewModel: AddApplicationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
    var matchScore by remember { mutableStateOf(0) }

    LaunchedEffect(jobId) {
        if (jobId != null) {
            val app = viewModel.getApplicationDirectly(jobId)
            if (app != null) {
                company = app.company
                jobTitle = app.jobTitle
                jobUrl = app.jobUrl
                location = app.location
                workMode = app.workMode
                source = app.source
                status = app.status
                dateApplied = app.dateApplied
                salary = app.salary
                recruiter = app.recruiter
                noticePeriod = app.noticePeriod
                jobDescription = app.jobDescription
                notes = app.notes
                matchScore = app.matchScore
            }
        }
    }

    val latestExtractedData by viewModel.latestExtractedData.collectAsState()

    LaunchedEffect(latestExtractedData) {
        latestExtractedData?.let { data ->
            company = data.company
            jobTitle = data.role
            location = data.location
            workMode = data.workMode
            salary = data.salaryRange
            noticePeriod = data.noticePeriod
            jobDescription = data.jobDescription
            jobUrl = data.jobUrl
            source = data.source
            recruiter = data.recruiter
            if (data.dateApplied.isNotBlank()) dateApplied = data.dateApplied
            matchScore = data.matchScore
            // For MVP, we can append skills into notes or job description, 
            // since we don't have a direct field for "Required Skills" yet.
            if (data.skills.isNotEmpty()) {
                notes = "AI Extracted Skills:\n${data.skills.joinToString(", ")}\n\n" + notes
            }
            viewModel.clearExtractedData()
        }
    }

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
                        text = if (jobId != null) "Edit Application" else "Add Application",
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
                            viewModel.saveApplication(
                                existingId = jobId,
                                company = company,
                                jobTitle = jobTitle,
                                jobUrl = jobUrl,
                                location = location,
                                workMode = workMode,
                                source = source,
                                status = status,
                                dateApplied = dateApplied,
                                salary = salary,
                                recruiter = recruiter,
                                noticePeriod = noticePeriod,
                                jobDescription = jobDescription,
                                notes = notes,
                                matchScore = matchScore
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5) // primary-container
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(stringResource(R.string.save_application), fontSize = 16.sp, color = Color.White)
                    }
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.cancel), fontSize = 16.sp, color = Color(0xFF464555)) // on-surface-variant
                    }
                }
            }
        }
    ) { paddingValues ->
        AddApplicationContent(
            modifier = Modifier.padding(paddingValues),
            onAnalyzeClick = onNavigateToAnalyzer,
            company = company, onCompanyChange = { company = it },
            jobTitle = jobTitle, onJobTitleChange = { jobTitle = it },
            jobUrl = jobUrl, onJobUrlChange = { jobUrl = it },
            location = location, onLocationChange = { location = it },
            workMode = workMode, onWorkModeChange = { workMode = it },
            source = source, onSourceChange = { source = it },
            status = status, onStatusChange = { status = it },
            dateApplied = dateApplied, onDateAppliedChange = { dateApplied = it },
            salary = salary, onSalaryChange = { salary = it },
            recruiter = recruiter, onRecruiterChange = { recruiter = it },
            noticePeriod = noticePeriod, onNoticePeriodChange = { noticePeriod = it },
            jobDescription = jobDescription, onJobDescriptionChange = { jobDescription = it },
            notes = notes, onNotesChange = { notes = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddApplicationContent(
    modifier: Modifier = Modifier,
    onAnalyzeClick: () -> Unit,
    company: String, onCompanyChange: (String) -> Unit,
    jobTitle: String, onJobTitleChange: (String) -> Unit,
    jobUrl: String, onJobUrlChange: (String) -> Unit,
    location: String, onLocationChange: (String) -> Unit,
    workMode: String, onWorkModeChange: (String) -> Unit,
    source: String, onSourceChange: (String) -> Unit,
    status: String, onStatusChange: (String) -> Unit,
    dateApplied: String, onDateAppliedChange: (String) -> Unit,
    salary: String, onSalaryChange: (String) -> Unit,
    recruiter: String, onRecruiterChange: (String) -> Unit,
    noticePeriod: String, onNoticePeriodChange: (String) -> Unit,
    jobDescription: String, onJobDescriptionChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()

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
                        Text(stringResource(R.string.save_time_with_ai), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF191C1E))
                        Text(stringResource(R.string.paste_a_job_description_and_let_ai_fill), fontSize = 14.sp, color = Color(0xFF464555))
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
                    Text(stringResource(R.string.analyze_job_description), fontSize = 14.sp)
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
                text = stringResource(R.string.or_enter_manually),
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
                FormInput(label = "Company Name *", value = company, onValueChange = onCompanyChange, placeholder = "e.g. Acme Corp")
                FormInput(label = "Job Title *", value = jobTitle, onValueChange = onJobTitleChange, placeholder = "e.g. Senior Product Designer")
                FormInput(label = "Job URL", value = jobUrl, onValueChange = onJobUrlChange, placeholder = "https://")
            }

            // Location & Logistics
            FormSection(title = "Location & Logistics") {
                FormInput(label = "Location", value = location, onValueChange = onLocationChange, placeholder = "City, State or Country")
                FormInput(label = "Work Mode", value = workMode, onValueChange = onWorkModeChange, placeholder = "Select mode")
                FormInput(label = "Source", value = source, onValueChange = onSourceChange, placeholder = "Where did you find this?")
            }

            // Application Status & Details
            FormSection(title = "Application Status & Details") {
                var statusExpanded by remember { mutableStateOf(false) }
                val statusOptions = listOf("Saved for later", "Applied", "Recruiter Contact", "Interviewing", "Offer Received", "Rejected")
                Box(modifier = Modifier.fillMaxWidth()) {
                    FormInput(
                        label = "Status", 
                        value = status, 
                        onValueChange = {}, 
                        placeholder = "Saved for later",
                        readOnly = true,
                        onClick = { statusExpanded = true },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Select Status")
                        }
                    )
                    androidx.compose.material3.DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onStatusChange(option)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                var showDatePicker by remember { mutableStateOf(false) }
                FormInput(
                    label = "Date Applied", 
                    value = dateApplied, 
                    onValueChange = onDateAppliedChange, 
                    placeholder = "DD/MM/YYYY",
                    readOnly = true,
                    onClick = { showDatePicker = true },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                )
                
                if (showDatePicker) {
                    val datePickerState = androidx.compose.material3.rememberDatePickerState()
                    androidx.compose.material3.DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val formattedDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(millis))
                                    onDateAppliedChange(formattedDate)
                                }
                                showDatePicker = false
                            }) { Text(stringResource(R.string.ok)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
                        }
                    ) {
                        androidx.compose.material3.DatePicker(state = datePickerState)
                    }
                }
                FormInput(label = "Salary Range / CTC", value = salary, onValueChange = onSalaryChange, placeholder = "e.g. $120k - $150k")
            }

            // Additional Context
            FormSection(title = "Additional Context") {
                FormInput(label = "Recruiter Info / Contact", value = recruiter, onValueChange = onRecruiterChange, placeholder = "Name or Email")
                FormInput(label = "Notice Period Required", value = noticePeriod, onValueChange = onNoticePeriodChange, placeholder = "e.g. 30 Days")
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.job_description), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E))
                    OutlinedTextField(
                        value = jobDescription,
                        onValueChange = onJobDescriptionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text(stringResource(R.string.paste_full_jd_here), color = Color(0xFF777587), fontSize = 14.sp) },
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
                    Text(stringResource(R.string.personal_notes), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        placeholder = { Text(stringResource(R.string.any_thoughts_or_prep_notes), color = Color(0xFF777587), fontSize = 14.sp) },
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
private fun FormInput(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit, 
    placeholder: String,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF191C1E))
        
        Box(modifier = Modifier.fillMaxWidth()) {
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
                singleLine = true,
                readOnly = readOnly,
                trailingIcon = trailingIcon
            )
            
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable(onClick = onClick)
                )
            }
        }
    }
}
