package com.sandeep.aijobapplicationtracker.presentation.screens.aifollowup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.presentation.components.AppButton
import com.sandeep.aijobapplicationtracker.presentation.components.AppCard
import com.sandeep.aijobapplicationtracker.presentation.components.EmptyStateView
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.presentation.components.SimpleMarkdownText
import com.sandeep.aijobapplicationtracker.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiFollowUpScreen(
    onNavigateBack: () -> Unit,
    viewModel: AiFollowUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Follow-Up") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.generateEmail() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
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
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoadingView()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Drafting follow-up email...")
                    }
                }
                is UiState.Error -> {
                    EmptyStateView(
                        title = "Error",
                        subtitle = s.message,
                        buttonText = "Retry",
                        onButtonClick = { viewModel.generateEmail() }
                    )
                }
                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Drafted Email",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        AppCard {
                            SimpleMarkdownText(
                                text = s.data,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        AppButton(
                            text = "Copy to Clipboard",
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Follow-up Email", s.data)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}
