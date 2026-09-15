package com.sandeep.aijobapplicationtracker.presentation.screens.aiassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.presentation.components.AppBottomBar
import com.sandeep.aijobapplicationtracker.presentation.components.BottomNavItem
import com.sandeep.aijobapplicationtracker.presentation.navigation.Screen
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    onNavigateToPrep: (String) -> Unit,
    onNavigateToFollowUp: (String) -> Unit,
    onNavigateToJobAnalyzer: () -> Unit,
    onNavigateToAiChat: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToApplications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToApplicationDetail: (String) -> Unit = {},
    viewModel: AiAssistantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val latestJobId by viewModel.latestJobId.collectAsState()

    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Screen.Home.route),
        BottomNavItem("Jobs", Icons.Filled.List, Screen.ApplicationsList.route),
        BottomNavItem("AI Prep", Icons.Filled.Star, Screen.AiAssistant.route),
        BottomNavItem("Profile", Icons.Filled.Person, Screen.ProfileSettings.route)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                items = bottomNavItems,
                currentRoute = Screen.AiAssistant.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Home.route -> onNavigateToHome()
                        Screen.ApplicationsList.route -> onNavigateToApplications()
                        Screen.ProfileSettings.route -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI Career Assistant",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "AI",
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your personalized job-search command center.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Top Feature Grid (AI Tools)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Analyze Job
                    AiToolCard(
                        modifier = Modifier.weight(1f),
                        title = "Analyze Job",
                        subtitle = "Match criteria",
                        iconBgColor = Color(0xFFEEF2FF),
                        iconTintColor = MaterialTheme.colorScheme.primaryContainer,
                        hoverColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                        onClick = onNavigateToJobAnalyzer
                    )
                    // AI Chat
                    AiToolCard(
                        modifier = Modifier.weight(1f),
                        title = "AI Chat",
                        subtitle = "Interactive Prep",
                        iconBgColor = Color(0xFFCFFAFE),
                        iconTintColor = MaterialTheme.colorScheme.secondary,
                        hoverColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        onClick = onNavigateToAiChat
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Mock Interview (Full Width)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clickable { latestJobId?.let { onNavigateToPrep(it) } }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Mock Interview",
                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Mock Interview",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "AI",
                                        tint = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "Practice with AI",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Go",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Next Actions Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Next Actions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View all",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onNavigateToApplications() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (uiState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            androidx.compose.material3.CircularProgressIndicator()
                        }
                    }
                    is UiState.Success -> {
                        val actions = (uiState as UiState.Success<List<NextActionItem>>).data
                        
                        if (actions.isEmpty()) {
                            Text(
                                text = "No pending actions right now. Good job!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            actions.forEach { action ->
                                ActionCard(
                                    modifier = Modifier.clickable { onNavigateToApplicationDetail(action.jobId) },
                                    title = action.title,
                                    subtitle = "${action.company} — ${action.description}",
                                    tagText = if (action.actionType == ActionType.FOLLOW_UP) "ACTION" else "PREP",
                                    tagColor = if (action.actionType == ActionType.FOLLOW_UP) Color(0xFFBA1A1A) else Color(0xFF92400E),
                                    tagBgColor = if (action.actionType == ActionType.FOLLOW_UP) Color(0xFFFFDAD6) else Color(0xFFFEF3C7),
                                    lineColor = if (action.actionType == ActionType.FOLLOW_UP) Color(0xFFBA1A1A) else Color(0xFFF59E0B)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(top = 12.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (action.actionType == ActionType.FOLLOW_UP) Color(0xFFFFDAD6).copy(alpha=0.3f) else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable {
                                                if (action.actionType == ActionType.PREPARE_INTERVIEW) {
                                                    onNavigateToPrep(action.jobId)
                                                } else if (action.actionType == ActionType.FOLLOW_UP) {
                                                    onNavigateToFollowUp(action.jobId)
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "AI",
                                            tint = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (action.actionType == ActionType.FOLLOW_UP) "Generate with AI" else "Prepare with AI",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                    is UiState.Empty -> {
                        Text(
                            text = "No pending actions right now. Good job!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = (uiState as UiState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {}
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun AiToolCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconBgColor: Color,
    iconTintColor: Color,
    hoverColor: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder since we don't have exact description/analytics icons in default filled
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = title,
                    tint = iconTintColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    tagText: String,
    tagColor: Color,
    tagBgColor: Color,
    lineColor: Color,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(lineColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(tagBgColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tagText,
                            style = MaterialTheme.typography.labelSmall,
                            color = tagColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                content()
            }
        }
    }
}
