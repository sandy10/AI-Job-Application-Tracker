package com.sandeep.aijobapplicationtracker.presentation.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.presentation.components.AppBottomBar
import com.sandeep.aijobapplicationtracker.presentation.components.AppCard
import com.sandeep.aijobapplicationtracker.presentation.components.BottomNavItem
import com.sandeep.aijobapplicationtracker.presentation.components.EmptyStateView
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.presentation.navigation.Screen
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToApplicationDetail: (String) -> Unit = {},
    onNavigateToApplications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAssistant: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        BottomNavItem(stringResource(R.string.nav_home), Icons.Filled.Home, Screen.Home.route),
        BottomNavItem(stringResource(R.string.nav_jobs), Icons.AutoMirrored.Filled.List, Screen.ApplicationsList.route),
        BottomNavItem(stringResource(R.string.nav_ai_prep), Icons.Filled.Star, Screen.AiAssistant.route),
        BottomNavItem(stringResource(R.string.nav_profile), Icons.Filled.Person, Screen.ProfileSettings.route)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                items = bottomNavItems,
                currentRoute = Screen.Home.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.ApplicationsList.route -> onNavigateToApplications()
                        Screen.AiAssistant.route -> onNavigateToAssistant()
                        Screen.ProfileSettings.route -> onNavigateToProfile()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToApplications,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_item))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val s = state) {
                is UiState.Idle, is UiState.Loading -> LoadingView()
                is UiState.Success -> HomeContent(
                    data = s.data,
                    onNavigateToApplicationDetail = onNavigateToApplicationDetail,
                    onNavigateToApplications = onNavigateToApplications,
                    onNavigateToNotifications = onNavigateToNotifications
                )

                is UiState.Empty -> EmptyStateView(
                    title = stringResource(id = R.string.no_data_title),
                    subtitle = stringResource(id = R.string.no_data_desc)
                )

                is UiState.Error -> EmptyStateView(
                    title = stringResource(id = R.string.error_title),
                    subtitle = s.message.ifEmpty { stringResource(id = R.string.unknown_error) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    data: HomeData,
    onNavigateToApplicationDetail: (String) -> Unit,
    onNavigateToApplications: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {


    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val hour =
                            java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                        val greeting = when {
                            hour < 5 -> stringResource(R.string.good_night)
                            hour < 12 -> stringResource(R.string.good_morning)
                            hour < 16 -> stringResource(R.string.good_afternoon)
                            hour < 21 -> stringResource(R.string.good_evening)
                            else -> stringResource(R.string.good_night)
                        }
                        Text(
                            text = stringResource(R.string.greeting_format, greeting, data.userName),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(id = R.string.home_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = stringResource(R.string.notifications),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (data.needsAttentionItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(8.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                            )
                        }
                    }
                }
            }
        }

        item {
            DashboardStatsSection(data)
        }

        if (data.needsAttentionItems.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.needs_attention),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 12.dp)
                )
            }
            items(data.needsAttentionItems) { item ->
                NeedsAttentionCard(
                    item = item,
                    onNavigateToDetail = onNavigateToApplicationDetail
                )
            }
        }

        if (data.recentApplications.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.recent_applications),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.view_all),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNavigateToApplications() }
                            .padding(4.dp)
                    )
                }
            }
            items(data.recentApplications) { app ->
                RecentApplicationCard(
                    app = app,
                    onClick = { onNavigateToApplicationDetail(app.id) }
                )
            }
        }
    }
}

@Composable
private fun DashboardStatsSection(data: HomeData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                count = data.totalApplications,
                label = stringResource(R.string.applications),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = data.totalInterviews,
                label = stringResource(R.string.interviews),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                count = data.totalFollowUps,
                label = stringResource(R.string.stat_followups),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = data.totalOffers,
                label = stringResource(R.string.offer_received),
                modifier = Modifier.weight(1f),
                countColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun StatCard(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    countColor: Color = MaterialTheme.colorScheme.onSurface
) {
    AppCard(modifier = modifier.height(100.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = countColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NeedsAttentionCard(
    item: AttentionItem,
    onNavigateToDetail: (String) -> Unit
) {
    val (iconColor, icon) = when (item.type) {
        AttentionType.URGENT -> MaterialTheme.colorScheme.error to Icons.Filled.Warning
        AttentionType.UPCOMING -> MaterialTheme.colorScheme.tertiary to Icons.Filled.Info
        AttentionType.INFO -> MaterialTheme.colorScheme.secondary to Icons.Filled.Info
    }

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        onClick = { onNavigateToDetail(item.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.ai_action),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onNavigateToDetail(item.id) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (item.type == AttentionType.URGENT) stringResource(R.string.generate_follow_up_button) else stringResource(R.string.prepare_with_ai),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RecentApplicationCard(app: JobApplicationItem, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_company_role_format, app.company, app.role),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.match_score_format, app.matchScore),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val locText = if (app.location.isNotBlank()) app.location else stringResource(R.string.location_unknown)
                val workText = if (app.workMode.isNotBlank()) app.workMode else stringResource(R.string.mode_unknown)
                val relativeTime = android.text.format.DateUtils.getRelativeTimeSpanString(
                    app.timestamp,
                    System.currentTimeMillis(),
                    android.text.format.DateUtils.MINUTE_IN_MILLIS
                ).toString()

                Text(
                    text = stringResource(R.string.loc_work_format, locText, workText),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = relativeTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ) {
                        val displayStatus = when (app.status) {
                            "Saved for later" -> stringResource(R.string.saved_for_later)
                            "Offer Received" -> stringResource(R.string.offer_received)
                            "Recruiter Contact" -> stringResource(R.string.recruiter_contact)
                            else -> app.status
                        }
                        Text(
                            text = displayStatus,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

