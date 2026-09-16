package com.sandeep.aijobapplicationtracker.presentation.screens.videointerview

import android.Manifest
import android.content.pm.PackageManager
import com.sandeep.aijobapplicationtracker.R
import androidx.compose.ui.res.stringResource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandeep.aijobapplicationtracker.presentation.components.LoadingView
import com.sandeep.aijobapplicationtracker.presentation.components.SimpleMarkdownText
import com.sandeep.aijobapplicationtracker.utils.FaceAnalyzer
import com.sandeep.aijobapplicationtracker.utils.VoiceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoInterviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: VideoInterviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val voiceHelper = remember { VoiceHelper(context) }
    
    // Connect VoiceHelper events to ViewModel
    DisposableEffect(Unit) {
        voiceHelper.onSpeechPartial = { viewModel.onPartialSpeechResult(it) }
        voiceHelper.onSpeechFinal = { viewModel.onAnswerRecorded(it) }
        voiceHelper.onSpeechError = { viewModel.onSpeechError(it) }
        voiceHelper.onTtsFinished = { viewModel.onTtsFinished() }
        
        onDispose {
            voiceHelper.destroy()
        }
    }

    // Handle states for TTS/STT dynamically
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is InterviewState.PromptingMicTest -> {
                voiceHelper.speak("Face detected. Please say something to test your microphone.")
            }
            is InterviewState.MicTestConfirmed -> {
                voiceHelper.speak("Audio confirmed. Starting interview.")
            }
            is InterviewState.AskingQuestion -> {
                voiceHelper.speak(state.question)
            }
            is InterviewState.ListeningToMicTest, is InterviewState.ListeningToAnswer -> {
                voiceHelper.startListening()
            }
            else -> {}
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            viewModel.onPermissionsGranted()
        }
    }

    LaunchedEffect(Unit) {
        val hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val hasAudio = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!hasCamera || !hasAudio) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        } else {
            viewModel.onPermissionsGranted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.video_ai_interview)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            val isCameraActive = uiState !is InterviewState.Initializing && 
                                 uiState !is InterviewState.SetupCamera && 
                                 uiState !is InterviewState.Finished && 
                                 uiState !is InterviewState.Error
            
            if (isCameraActive) {
                val isSplitScreen = uiState is InterviewState.ListeningToAnswer || uiState is InterviewState.ListeningToMicTest
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (isSplitScreen) 0.5f else 1f)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                
                                val faceAnalyzer = FaceAnalyzer { isDetected ->
                                    viewModel.onFaceDetected(isDetected)
                                }
                                
                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(Dispatchers.Default.asExecutor(), faceAnalyzer)
                                    }
                                    
                                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                                
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner, cameraSelector, preview, imageAnalysis
                                    )
                                } catch (e: Exception) {
                                    // Fallback or log
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            
                            previewView
                        }
                    )
                    
                    // Overlays based on state
                    when (val state = uiState) {
                        is InterviewState.WaitingForFace -> {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Placeholder icon
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(64.dp).padding(bottom = 16.dp)
                                    )
                                    Text(stringResource(R.string.please_position_your_face_in_the_camera), color = Color.White, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                        is InterviewState.PromptingMicTest, is InterviewState.MicTestConfirmed -> {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (state is InterviewState.PromptingMicTest) "Testing Microphone..." else "Audio Confirmed!", color = Color.White, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        is InterviewState.GeneratingQuestions -> {
                            LoadingOverlay(state.progressText)
                        }
                        is InterviewState.AskingQuestion -> {
                            InterviewOverlay("Question ${state.index} of ${state.total}", state.question, isListening = false)
                        }
                        is InterviewState.ProcessingAnswer -> {
                            LoadingOverlay("Saving answer...")
                        }
                        is InterviewState.Evaluating -> {
                            LoadingOverlay(state.progressText)
                        }
                        else -> {}
                    }
                }
                
                if (isSplitScreen) {
                    val questionText = if (uiState is InterviewState.ListeningToAnswer) (uiState as InterviewState.ListeningToAnswer).question else "Say something to test your microphone..."
                    val partialAnswerText = if (uiState is InterviewState.ListeningToAnswer) (uiState as InterviewState.ListeningToAnswer).partialAnswer else (uiState as InterviewState.ListeningToMicTest).partialAnswer
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        Text(if (uiState is InterviewState.ListeningToAnswer) "Question:" else "Mic Test:", style = MaterialTheme.typography.labelMedium)
                        Text(questionText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(stringResource(R.string.your_voice), style = MaterialTheme.typography.labelMedium)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = if (partialAnswerText.isBlank()) "Listening..." else partialAnswerText, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var isMuted by remember { mutableStateOf(false) }
                            Button(
                                onClick = { 
                                    isMuted = !isMuted
                                    if (isMuted) voiceHelper.stopListening() else voiceHelper.startListening()
                                }
                            ) {
                                Text(if (isMuted) "Unmute" else "Mute")
                            }
                            
                            Button(
                                onClick = { 
                                    voiceHelper.stopListening() 
                                    viewModel.onAnswerRecorded(partialAnswerText) 
                                }
                            ) {
                                Text(if (uiState is InterviewState.ListeningToAnswer) "Done Speaking" else "Confirm Audio")
                            }
                        }
                    }
                }
            } else if (uiState is InterviewState.Finished) {
                val state = uiState as InterviewState.Finished
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(stringResource(R.string.interview_complete), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        SimpleMarkdownText(text = state.feedbackMarkdown, modifier = Modifier.padding(16.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.back_to_application))
                    }
                }
            } else if (uiState is InterviewState.Error) {
                val state = uiState as InterviewState.Error
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.setting_up_camera), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LoadingOverlay(text: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text, color = Color.White)
        }
    }
}

@Composable
fun InterviewOverlay(title: String, question: String, isListening: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = MaterialTheme.colorScheme.primaryContainer, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(question, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}
