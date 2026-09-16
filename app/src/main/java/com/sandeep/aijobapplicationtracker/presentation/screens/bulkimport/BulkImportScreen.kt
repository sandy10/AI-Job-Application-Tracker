package com.sandeep.aijobapplicationtracker.presentation.screens.bulkimport

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icons.AutoMirrored.Filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.presentation.components.AppButton
import com.sandeep.aijobapplicationtracker.presentation.components.AppTextField
import com.sandeep.aijobapplicationtracker.presentation.components.AppCard
import androidx.compose.material3.TopAppBar
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkImportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: BulkImportViewModel = hiltViewModel()
) {
    var jobsCsv by remember { mutableStateOf("") }
    var draftsCsv by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bulk_import_data)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.bulk_import_desc),
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AppTextField(
                    value = jobsCsv,
                    onValueChange = { jobsCsv = it },
                    label = stringResource(R.string.jobs_csv_data),
                    placeholder = stringResource(R.string.jobs_csv_placeholder),
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AppTextField(
                    value = draftsCsv,
                    onValueChange = { draftsCsv = it },
                    label = stringResource(R.string.drafts_csv_data),
                    placeholder = stringResource(R.string.drafts_csv_placeholder),
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                is UiState.Success -> {
                    Text(
                        text = stringResource(R.string.import_successful),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    LaunchedEffect(Unit) {
                        onNavigateToHome()
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {}
            }

            AppButton(
                text = stringResource(R.string.start_ingestion),
                onClick = { viewModel.ingestData(jobsCsv, draftsCsv) },
                enabled = uiState !is UiState.Loading && (jobsCsv.isNotBlank() || draftsCsv.isNotBlank()),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
