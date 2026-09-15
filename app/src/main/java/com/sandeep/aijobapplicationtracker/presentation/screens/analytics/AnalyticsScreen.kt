package com.sandeep.aijobapplicationtracker.presentation.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.presentation.components.AppCard
import com.sandeep.aijobapplicationtracker.presentation.components.EmptyStateView
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Search Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = uiState) {
                is UiState.Idle, is UiState.Loading -> LoadingView()
                is UiState.Error -> EmptyStateView(
                    title = "Error",
                    subtitle = s.message,
                    buttonText = "Go back",
                    onButtonClick = onNavigateBack
                )
                is UiState.Empty -> EmptyStateView(
                    title = "No Data",
                    subtitle = "Add some applications to see analytics.",
                    buttonText = "Go back",
                    onButtonClick = onNavigateBack
                )
                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "JOB SEARCH PERFORMANCE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        AppCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                StatRow("Applications", s.data.totalApplications.toString())
                                StatRow("Recruiter Responses", s.data.totalResponses.toString())
                                StatRow("Interviews", s.data.totalInterviews.toString())
                                StatRow("Offers", s.data.totalOffers.toString())
                            }
                        }

                        Text(
                            text = "CONVERSION RATES",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        AppCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                RateRow("Application → Response", s.data.appToResponseRate)
                                RateRow("Response → Interview", s.data.responseToInterviewRate)
                                RateRow("Interview → Offer", s.data.interviewToOfferRate)
                            }
                        }

                        Text(
                            text = "APPLICATIONS BY SOURCE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        AppCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                s.data.applicationsBySource.forEach { (source, count) ->
                                    StatRow(source, count.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RateRow(label: String, percentage: Int) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
