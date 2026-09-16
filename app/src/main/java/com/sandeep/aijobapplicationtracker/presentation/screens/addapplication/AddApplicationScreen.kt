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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(jobId) {
        if (jobId != null) {
            viewModel.onEvent(AddApplicationEvent.LoadApplication(jobId))
        }
    }

    val latestExtractedData by viewModel.latestExtractedData.collectAsStateWithLifecycle()

    LaunchedEffect(latestExtractedData) {
        latestExtractedData?.let { data ->
            viewModel.onEvent(AddApplicationEvent.ExtractedDataReceived(data))
            viewModel.clearExtractedData()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onNavigateBack()
        } else if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
            viewModel.onEvent(AddApplicationEvent.ClearError)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // bg-background
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (jobId != null) stringResource(R.string.edit_application) else stringResource(R.string.add_application_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            viewModel.onEvent(AddApplicationEvent.SaveClicked(existingId = jobId))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary // primary-container
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(stringResource(R.string.save_application), fontSize = 16.sp, color = MaterialTheme.colorScheme.surface)
                    }
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.cancel), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) // on-surface-variant
                    }
                }
            }
        }
    ) { paddingValues ->
        AddApplicationContent(
            modifier = Modifier.padding(paddingValues),
            onAnalyzeClick = onNavigateToAnalyzer,
            company = formState.company, onCompanyChange = { viewModel.onEvent(AddApplicationEvent.CompanyChanged(it)) },
            jobTitle = formState.jobTitle, onJobTitleChange = { viewModel.onEvent(AddApplicationEvent.JobTitleChanged(it)) },
            jobUrl = formState.jobUrl, onJobUrlChange = { viewModel.onEvent(AddApplicationEvent.JobUrlChanged(it)) },
            location = formState.location, onLocationChange = { viewModel.onEvent(AddApplicationEvent.LocationChanged(it)) },
            workMode = formState.workMode, onWorkModeChange = { viewModel.onEvent(AddApplicationEvent.WorkModeChanged(it)) },
            source = formState.source, onSourceChange = { viewModel.onEvent(AddApplicationEvent.SourceChanged(it)) },
            status = formState.status, onStatusChange = { viewModel.onEvent(AddApplicationEvent.StatusChanged(it)) },
            dateApplied = formState.dateApplied, onDateAppliedChange = { viewModel.onEvent(AddApplicationEvent.DateAppliedChanged(it)) },
            salary = formState.salary, onSalaryChange = { viewModel.onEvent(AddApplicationEvent.SalaryChanged(it)) },
            recruiter = formState.recruiter, onRecruiterChange = { viewModel.onEvent(AddApplicationEvent.RecruiterChanged(it)) },
            noticePeriod = formState.noticePeriod, onNoticePeriodChange = { viewModel.onEvent(AddApplicationEvent.NoticePeriodChanged(it)) },
            jobDescription = formState.jobDescription, onJobDescriptionChange = { viewModel.onEvent(AddApplicationEvent.JobDescriptionChanged(it)) },
            notes = formState.notes, onNotesChange = { viewModel.onEvent(AddApplicationEvent.NotesChanged(it)) }
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
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(stringResource(R.string.save_time_with_ai), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                        Text(stringResource(R.string.paste_a_job_description_and_let_ai_fill), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
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
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
            Text(
                text = stringResource(R.string.or_enter_manually),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        }

        // Form Fields
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Basic Details
            FormSection(title = stringResource(R.string.basic_details)) {
                FormInput(label = stringResource(R.string.company_name_req), value = company, onValueChange = onCompanyChange, placeholder = stringResource(R.string.eg_acme_corp))
                FormInput(label = stringResource(R.string.job_title_req), value = jobTitle, onValueChange = onJobTitleChange, placeholder = stringResource(R.string.eg_senior_product_designer))
                FormInput(label = stringResource(R.string.job_url_label), value = jobUrl, onValueChange = onJobUrlChange, placeholder = stringResource(R.string.job_url_https))
            }

            // Location & Logistics
            FormSection(title = stringResource(R.string.location_logistics)) {
                FormInput(label = stringResource(R.string.location_label), value = location, onValueChange = onLocationChange, placeholder = stringResource(R.string.location_ph))
                FormInput(label = stringResource(R.string.work_mode_label), value = workMode, onValueChange = onWorkModeChange, placeholder = stringResource(R.string.select_mode))
                FormInput(label = stringResource(R.string.source_label), value = source, onValueChange = onSourceChange, placeholder = stringResource(R.string.source_ph))
            }

            // Application Status & Details
            FormSection(title = stringResource(R.string.application_status_details)) {
                var statusExpanded by remember { mutableStateOf(false) }
                val statusOptions = listOf(
                    stringResource(R.string.saved_for_later),
                    stringResource(R.string.status_applied),
                    stringResource(R.string.recruiter_contact),
                    stringResource(R.string.status_interviewing),
                    stringResource(R.string.offer_received),
                    stringResource(R.string.status_rejected)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    FormInput(
                        label = stringResource(R.string.status_label), 
                        value = status, 
                        onValueChange = {}, 
                        placeholder = stringResource(R.string.saved_for_later),
                        readOnly = true,
                        onClick = { statusExpanded = true },
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = stringResource(R.string.select_status))
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
                    label = stringResource(R.string.date), 
                    value = dateApplied, 
                    onValueChange = onDateAppliedChange, 
                    placeholder = stringResource(R.string.date_applied_ph),
                    readOnly = true,
                    onClick = { showDatePicker = true },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.select_date))
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
                FormInput(label = stringResource(R.string.salary_range_ctc), value = salary, onValueChange = onSalaryChange, placeholder = stringResource(R.string.eg_salary))
            }

            // Additional Context
            FormSection(title = stringResource(R.string.additional_context)) {
                FormInput(label = stringResource(R.string.recruiter_info), value = recruiter, onValueChange = onRecruiterChange, placeholder = stringResource(R.string.recruiter_ph))
                FormInput(label = stringResource(R.string.notice_period_req), value = noticePeriod, onValueChange = onNoticePeriodChange, placeholder = stringResource(R.string.eg_notice_period))
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.job_description), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                    OutlinedTextField(
                        value = jobDescription,
                        onValueChange = onJobDescriptionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text(stringResource(R.string.paste_full_jd_here), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.personal_notes), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        placeholder = { Text(stringResource(R.string.any_thoughts_or_prep_notes), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
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
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
        
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, // surface-container-low
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline, // outline-variant
                    focusedBorderColor = MaterialTheme.colorScheme.primary // primary
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

