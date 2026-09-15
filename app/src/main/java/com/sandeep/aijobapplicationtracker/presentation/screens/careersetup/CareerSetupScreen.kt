package com.sandeep.aijobapplicationtracker.presentation.screens.careersetup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.presentation.components.AppButton
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CareerSetupScreen(
    onNavigateToHome: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: CareerSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val initialProfile by viewModel.userProfile.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var targetRole by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableStateOf("") }
    var yearsExp by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var workPreference by remember { mutableStateOf("remote") }
    var currentCtc by remember { mutableStateOf("") }
    var expectedCtc by remember { mutableStateOf("") }
    var noticePeriod by remember { mutableStateOf("") }
    
    var skillsList by remember { mutableStateOf(listOf<String>()) }
    var newSkill by remember { mutableStateOf("") }
    var expDropdownExpanded by remember { mutableStateOf(false) }

    // Per-field error states for inline validation
    var nameError by remember { mutableStateOf<String?>(null) }
    var targetRoleError by remember { mutableStateOf<String?>(null) }
    var experienceLevelError by remember { mutableStateOf<String?>(null) }
    var yearsExpError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    var isEditMode by remember { mutableStateOf(false) }
    var hasInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(initialProfile) {
        initialProfile?.let { profile ->
            if (!hasInitialized) {
                isEditMode = profile.targetRole.isNotBlank()
                hasInitialized = true
            }
            
            if (fullName.isBlank()) fullName = profile.name
            if (targetRole.isBlank()) targetRole = profile.targetRole
            if (experienceLevel.isBlank()) experienceLevel = profile.experienceLevel
            if (yearsExp.isBlank()) yearsExp = profile.yearsExperience
            if (location.isBlank()) location = profile.location
            if (profile.workPreference.isNotBlank()) workPreference = profile.workPreference
            if (currentCtc.isBlank()) currentCtc = profile.currentCtc
            if (expectedCtc.isBlank()) expectedCtc = profile.expectedCtc
            if (noticePeriod.isBlank()) noticePeriod = profile.noticePeriod
            if (skillsList.isEmpty() && profile.skills.isNotEmpty()) skillsList = profile.skills
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            if (isEditMode) {
                onBackClick() // Pop back to Profile Screen
            } else {
                onNavigateToHome()
            }
        } else if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 48.dp, bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.offset(x = (-12).dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (!isEditMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = stringResource(id = R.string.step_1_of_1),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = if (isEditMode) "Edit Profile" else stringResource(id = R.string.career_setup_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                
                if (!isEditMode) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = stringResource(id = R.string.career_setup_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                AppButton(
                    text = if (isEditMode) "Update profile" else stringResource(id = R.string.continue_to_dashboard),
                    onClick = {
                        // Inline field-level validation
                        val errorMsg = context.getString(R.string.field_required)
                        nameError = if (fullName.isBlank()) errorMsg else null
                        targetRoleError = if (targetRole.isBlank()) errorMsg else null
                        experienceLevelError = if (experienceLevel.isBlank()) errorMsg else null
                        yearsExpError = if (yearsExp.isBlank()) errorMsg else null
                        locationError = if (location.isBlank()) errorMsg else null

                        // Only proceed if no errors
                        val hasErrors = nameError != null || targetRoleError != null ||
                            experienceLevelError != null || yearsExpError != null || locationError != null
                        if (!hasErrors) {
                            hasInitialized = true
                            viewModel.saveProfile(
                                name = fullName,
                                experienceLevel = experienceLevel,
                                yearsOfExperience = yearsExp,
                                primaryRole = targetRole,
                                skills = skillsList,
                                location = location,
                                currentCtc = currentCtc,
                                expectedCtc = expectedCtc,
                                noticePeriod = noticePeriod,
                                workPreference = workPreference
                            )
                        }
                    },
                    isLoading = uiState is UiState.Loading
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Full Name
            CustomTextField(
                label = stringResource(id = R.string.name_label),
                value = fullName,
                onValueChange = { fullName = it; nameError = null },
                placeholder = stringResource(id = R.string.name_placeholder),
                errorMessage = nameError
            )

            // Target Role
            CustomTextField(
                label = stringResource(id = R.string.target_role_label),
                value = targetRole,
                onValueChange = { targetRole = it; targetRoleError = null },
                placeholder = stringResource(id = R.string.target_role_placeholder),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                },
                errorMessage = targetRoleError
            )

            // Exp Level & Years
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val expOptions = listOf(
                    "Student / Intern",
                    "Fresher / Entry-Level",
                    "Junior / Associate",
                    "Mid-Level",
                    "Senior",
                    "Lead",
                    "Manager",
                    "Director / Head",
                    "Executive / C-Suite",
                    "Freelance / Consultant",
                    "Career Break",
                    "Other"
                )                
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = expDropdownExpanded,
                    onExpandedChange = { expDropdownExpanded = !expDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    CustomTextField(
                        modifier = Modifier.menuAnchor(),
                        label = stringResource(id = R.string.experience_level_label),
                        value = experienceLevel,
                        onValueChange = {}, // Read-only
                        placeholder = stringResource(id = R.string.experience_level_placeholder),
                        readOnly = true,
                        trailingIcon = {
                            androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = expDropdownExpanded)
                        },
                        errorMessage = experienceLevelError
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expDropdownExpanded,
                        onDismissRequest = { expDropdownExpanded = false }
                    ) {
                        expOptions.forEach { selectionOption ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    experienceLevel = selectionOption
                                    experienceLevelError = null
                                    expDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                CustomTextField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = R.string.years_experience_label),
                    value = yearsExp,
                    onValueChange = { yearsExp = it; yearsExpError = null },
                    placeholder = stringResource(id = R.string.years_experience_placeholder),
                    errorMessage = yearsExpError
                )
            }

            // AI Skills Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                // Left border accent
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .align(Alignment.CenterStart)
                )

                // Sparkle
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(16.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.primary_skills_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(id = R.string.ai_extracted_skills_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        skillsList.forEach { skill ->
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFFEEF2FF), CircleShape)
                                    .border(1.dp, Color(0xFFC7D2FE), CircleShape)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = skill,
                                    color = Color(0xFF4F46E5),
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { skillsList = skillsList - skill }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newSkill,
                            onValueChange = { newSkill = it },
                            modifier = Modifier.weight(1f).height(48.dp),
                            placeholder = { Text(stringResource(R.string.e_g_skills), style = MaterialTheme.typography.bodySmall) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        Button(
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { 
                                if (newSkill.isNotBlank() && !skillsList.contains(newSkill.trim())) {
                                    skillsList = skillsList + newSkill.trim()
                                    newSkill = ""
                                }
                            }
                        ) {
                            Text(stringResource(id = R.string.add_skill_button))
                        }
                    }
                }
            }

            // Location
            CustomTextField(
                label = stringResource(id = R.string.location_label),
                value = location,
                onValueChange = { location = it; locationError = null },
                placeholder = stringResource(id = R.string.location_placeholder),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                },
                errorMessage = locationError
            )

            // Optional CTC and Notice Period Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = R.string.current_ctc_label),
                    value = currentCtc,
                    onValueChange = { currentCtc = it },
                    placeholder = stringResource(id = R.string.current_ctc_placeholder)
                )
                CustomTextField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = R.string.expected_ctc_label),
                    value = expectedCtc,
                    onValueChange = { expectedCtc = it },
                    placeholder = stringResource(id = R.string.expected_ctc_placeholder)
                )
            }
            
            CustomTextField(
                label = stringResource(id = R.string.notice_period_label),
                value = noticePeriod,
                onValueChange = { noticePeriod = it },
                placeholder = stringResource(id = R.string.notice_period_placeholder)
            )

            // Work Preference Segmented
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(id = R.string.work_preference_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SegmentOption(
                        text = stringResource(id = R.string.work_pref_remote),
                        isSelected = workPreference == "remote",
                        onClick = { workPreference = "remote" },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentOption(
                        text = stringResource(id = R.string.work_pref_hybrid),
                        isSelected = workPreference == "hybrid",
                        onClick = { workPreference = "hybrid" },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentOption(
                        text = stringResource(id = R.string.work_pref_onsite),
                        isSelected = workPreference == "onsite",
                        onClick = { workPreference = "onsite" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            enabled = enabled,
            isError = errorMessage != null,
            placeholder = { 
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.outline
                ) 
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedBorderColor = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            singleLine = true
        )
        // Show inline error message in red below the field
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
