package com.sandeep.aijobapplicationtracker.presentation.screens.applications

import androidx.compose.ui.res.stringResource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.presentation.components.AppBottomBar
import com.sandeep.aijobapplicationtracker.presentation.components.BottomNavItem
import com.sandeep.aijobapplicationtracker.presentation.components.EmptyStateView
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.presentation.navigation.Screen
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsListScreen(
    onNavigateToAddJob: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToAssistant: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: ApplicationsListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedSortOption by viewModel.selectedSortOption.collectAsStateWithLifecycle()
    val selectedWorkMode by viewModel.selectedWorkMode.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val bottomNavItems = listOf(
        BottomNavItem(stringResource(R.string.nav_home), Icons.Filled.Home, Screen.Home.route),
        BottomNavItem(stringResource(R.string.nav_jobs), Icons.AutoMirrored.Filled.List, Screen.ApplicationsList.route),
        BottomNavItem(stringResource(R.string.nav_ai_prep), Icons.Filled.Star, Screen.AiAssistant.route),
        BottomNavItem(stringResource(R.string.nav_profile), Icons.Filled.Person, Screen.ProfileSettings.route)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // bg-background
        bottomBar = {
            AppBottomBar(
                items = bottomNavItems,
                currentRoute = Screen.ApplicationsList.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Home.route -> onNavigateToHome()
                        Screen.AiAssistant.route -> onNavigateToAssistant()
                        Screen.ProfileSettings.route -> onNavigateToProfile()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddJob,
                containerColor = MaterialTheme.colorScheme.primary, // bg-primary
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp, end = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        if (showFilterSheet) {
            androidx.compose.material3.ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(stringResource(R.string.sort_by), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.material3.FilterChip(
                            selected = selectedSortOption == SortOption.RECENTLY_ADDED,
                            onClick = { viewModel.setSortOption(SortOption.RECENTLY_ADDED) },
                            label = { Text(stringResource(R.string.recently_added)) }
                        )
                        androidx.compose.material3.FilterChip(
                            selected = selectedSortOption == SortOption.MATCH_SCORE,
                            onClick = { viewModel.setSortOption(SortOption.MATCH_SCORE) },
                            label = { Text(stringResource(R.string.match_score)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(stringResource(R.string.work_mode), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("All", "Remote", "Hybrid", "Onsite").forEach { mode ->
                            androidx.compose.material3.FilterChip(
                                selected = selectedWorkMode == mode,
                                onClick = { viewModel.setWorkMode(mode) },
                                label = { Text(mode) }
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background) // bg-surface
                    .padding(horizontal = 20.dp)
                    .padding(top = 48.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.applications),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showFilterSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List, // fallback for filter_list
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant // on-surface-variant
                    )
                }
            }
            
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_company_or_role), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FilterChipsRow(selectedFilter) { viewModel.setFilter(it) }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (val s = uiState) {
                is UiState.Idle, is UiState.Loading -> LoadingView()
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(s.data.filter { it.company.contains(searchQuery, ignoreCase = true) || it.role.contains(searchQuery, ignoreCase = true) }) { app ->
                            DetailedApplicationCard(
                                app = app,
                                onClick = { onNavigateToDetail(app.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
                is UiState.Empty -> EmptyStateView(
                    title = stringResource(R.string.no_apps_found),
                    subtitle = stringResource(R.string.try_adjusting_search)
                )
                is UiState.Error -> EmptyStateView(
                    title = stringResource(R.string.error_title),
                    subtitle = s.message.ifEmpty { stringResource(R.string.unknown_error) }
                )
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: ApplicationStatus,
    onFilterSelected: (ApplicationStatus) -> Unit
) {
    val filters = listOf(
        ApplicationStatus.ALL to stringResource(R.string.filter_all_label),
        ApplicationStatus.SAVED to stringResource(R.string.filter_saved),
        ApplicationStatus.APPLIED to stringResource(R.string.filter_applied),
        ApplicationStatus.RECRUITER to stringResource(R.string.recruiter),
        ApplicationStatus.INTERVIEW to stringResource(R.string.filter_interview),
        ApplicationStatus.OFFER to stringResource(R.string.filter_offer),
        ApplicationStatus.REJECTED to stringResource(R.string.filter_rejected)
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (status, label) ->
            val isSelected = status == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White // primary-container vs surface
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onFilterSelected(status) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant, // on-primary-container vs on-surface-variant
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DetailedApplicationCard(
    app: DetailedJobApplication,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, // Use info or appropriate placeholder
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = app.role,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface // on-background
                    )
                    Text(
                        text = stringResource(R.string.loc_work_format, app.company, app.location),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // on-surface-variant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusColor = when(app.status) {
                    ApplicationStatus.INTERVIEW -> MaterialTheme.colorScheme.tertiary
                    ApplicationStatus.APPLIED -> MaterialTheme.colorScheme.onSurfaceVariant
                    ApplicationStatus.SAVED -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.primary
                }
                val statusBg = when(app.status) {
                    ApplicationStatus.INTERVIEW -> MaterialTheme.colorScheme.tertiaryContainer
                    ApplicationStatus.APPLIED -> MaterialTheme.colorScheme.surfaceVariant // surface-container-high
                    ApplicationStatus.SAVED -> MaterialTheme.colorScheme.surfaceVariant // surface-variant
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBg)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = app.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }
                
                if (app.matchScore > 0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "AI Match",
                            tint = androidx.compose.ui.graphics.Color.White, // primary
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${app.matchScore}% Match",
                            fontSize = 12.sp,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val relativeTime = android.text.format.DateUtils.getRelativeTimeSpanString(
                    app.timestamp,
                    System.currentTimeMillis(),
                    android.text.format.DateUtils.MINUTE_IN_MILLIS
                ).toString().lowercase(java.util.Locale.getDefault())

                val (icon, text, color) = when(app.status) {
                    ApplicationStatus.INTERVIEW -> Triple(Icons.Default.Star, stringResource(R.string.interview_scheduled), MaterialTheme.colorScheme.tertiary)
                    ApplicationStatus.APPLIED -> Triple(Icons.Default.Info, stringResource(R.string.applied_time_format, relativeTime), MaterialTheme.colorScheme.onSurfaceVariant)
                    ApplicationStatus.SAVED -> Triple(Icons.Default.Info, stringResource(R.string.saved_time_format, relativeTime), MaterialTheme.colorScheme.onSurfaceVariant)
                    else -> Triple(Icons.Default.Info, stringResource(R.string.updated_time_format, relativeTime), MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = text,
                    fontSize = 14.sp,
                    color = color,
                    fontWeight = if (app.status == ApplicationStatus.INTERVIEW) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}

