package com.sandeep.aijobapplicationtracker.presentation.screens.applications

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
import androidx.compose.material.icons.filled.List
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
import androidx.compose.runtime.collectAsState
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
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()
    val selectedWorkMode by viewModel.selectedWorkMode.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Screen.Home.route),
        BottomNavItem("Jobs", Icons.Filled.List, Screen.ApplicationsList.route),
        BottomNavItem("AI Prep", Icons.Filled.Star, Screen.AiAssistant.route),
        BottomNavItem("Profile", Icons.Filled.Person, Screen.ProfileSettings.route)
    )

    Scaffold(
        containerColor = Color(0xFFF7F9FB), // bg-background
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
                containerColor = Color(0xFF3525CD), // bg-primary
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
                    Text("Sort By", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.material3.FilterChip(
                            selected = selectedSortOption == SortOption.RECENTLY_ADDED,
                            onClick = { viewModel.setSortOption(SortOption.RECENTLY_ADDED) },
                            label = { Text("Recently Added") }
                        )
                        androidx.compose.material3.FilterChip(
                            selected = selectedSortOption == SortOption.MATCH_SCORE,
                            onClick = { viewModel.setSortOption(SortOption.MATCH_SCORE) },
                            label = { Text("Match Score") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Work Mode", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1E))
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
                    .background(Color(0xFFF7F9FB)) // bg-surface
                    .padding(horizontal = 20.dp)
                    .padding(top = 48.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Applications",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191C1E)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showFilterSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List, // fallback for filter_list
                        contentDescription = "Filter",
                        tint = Color(0xFF464555) // on-surface-variant
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
                placeholder = { Text("Search company or role", color = Color(0xFF64748B), fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF64748B)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = Color(0xFF3525CD)
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
                    title = "No Applications Found",
                    subtitle = "Try adjusting your search or filters"
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
private fun FilterChipsRow(
    selectedFilter: ApplicationStatus,
    onFilterSelected: (ApplicationStatus) -> Unit
) {
    val filters = listOf(
        ApplicationStatus.ALL to "All",
        ApplicationStatus.SAVED to "Saved",
        ApplicationStatus.APPLIED to "Applied",
        ApplicationStatus.RECRUITER to "Recruiter",
        ApplicationStatus.INTERVIEW to "Interview",
        ApplicationStatus.OFFER to "Offer",
        ApplicationStatus.REJECTED to "Rejected"
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
                        color = if (isSelected) Color(0xFF4F46E5) else Color.White // primary-container vs surface
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onFilterSelected(status) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color(0xFFDAD7FF) else Color(0xFF464555), // on-primary-container vs on-surface-variant
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
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
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
                        .background(Color(0xFFECEEF0)) // surface-container
                        .border(1.dp, Color(0xFFC7C4D8), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, // Use info or appropriate placeholder
                        contentDescription = "Logo",
                        tint = Color(0xFF777587),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = app.role,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF191C1E) // on-background
                    )
                    Text(
                        text = "${app.company} • ${app.location}",
                        fontSize = 14.sp,
                        color = Color(0xFF464555) // on-surface-variant
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
                    ApplicationStatus.INTERVIEW -> Color(0xFFD97706)
                    ApplicationStatus.APPLIED -> Color(0xFF464555)
                    ApplicationStatus.SAVED -> Color(0xFF464555)
                    else -> Color(0xFF3525CD)
                }
                val statusBg = when(app.status) {
                    ApplicationStatus.INTERVIEW -> Color(0xFFFEF3C7)
                    ApplicationStatus.APPLIED -> Color(0xFFE6E8EA) // surface-container-high
                    ApplicationStatus.SAVED -> Color(0xFFE0E3E5) // surface-variant
                    else -> Color(0xFFECEEF0)
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
                            .background(Color(0xFFEEF2FF))
                            .border(
                                width = 1.dp,
                                color = Color(0xFF3525CD).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "AI Match",
                            tint = Color(0xFF3525CD), // primary
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${app.matchScore}% Match",
                            fontSize = 12.sp,
                            color = Color(0xFF3525CD),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val (icon, text, color) = when(app.status) {
                    ApplicationStatus.INTERVIEW -> Triple(Icons.Default.Star, "Interview tomorrow • 11:00 AM", Color(0xFFD97706))
                    ApplicationStatus.APPLIED -> Triple(Icons.Default.Info, "Applied 4 days ago", Color(0xFF464555))
                    ApplicationStatus.SAVED -> Triple(Icons.Default.Info, "Saved yesterday", Color(0xFF464555))
                    else -> Triple(Icons.Default.Info, "Updated recently", Color(0xFF464555))
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

