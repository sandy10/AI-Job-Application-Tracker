package com.sandeep.aijobapplicationtracker.presentation.screens.signin

import android.app.Activity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import com.sandeep.aijobapplicationtracker.utils.hideKeyboard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sandeep.aijobapplicationtracker.R
import com.sandeep.aijobapplicationtracker.presentation.components.AppButton
import com.sandeep.aijobapplicationtracker.presentation.components.AppCard
import com.sandeep.aijobapplicationtracker.presentation.components.AppSecondaryButton
import com.sandeep.aijobapplicationtracker.presentation.components.AppTextField
import com.sandeep.aijobapplicationtracker.utils.UiState
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * The web client ID from google-services.json (client_type = 3).
 * This is used by Credential Manager to request a Google ID token.
 */
private const val WEB_CLIENT_ID = "294862957055-2mq0uqjqjbgdnb7dv9699eef5me8nj0o.apps.googleusercontent.com"

@Composable
fun SignInScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToCareerSetup: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Navigate on successful sign-in
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val isComplete = (uiState as UiState.Success).data
            if (isComplete) {
                onNavigateToHome()
            } else {
                onNavigateToCareerSetup()
            }
        } else if (uiState is UiState.Error) {
            snackbarHostState.showSnackbar((uiState as UiState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        SignInContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onSignInClick = { email, pass -> viewModel.signIn(email, pass) },
            onSignUpClick = { email, pass -> viewModel.signUp(email, pass) },
            onForgotPasswordClick = { email, onResult ->
                viewModel.resetPassword(email) { success, msg ->
                    onResult(success)
                    coroutineScope.launch {
                        if (success) {
                            snackbarHostState.showSnackbar("Password reset email sent to $email")
                        } else {
                            snackbarHostState.showSnackbar(msg ?: "Failed to send reset email")
                        }
                    }
                }
            },
            onGoogleSignInClick = {
                // Launch Google Sign-In using Credential Manager
                coroutineScope.launch {
                    viewModel.setLoading()
                    try {
                        val credentialManager = CredentialManager.create(context)

                        // Build the Google ID option requesting an ID token
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(WEB_CLIENT_ID)
                            .build()

                        // Build the credential request
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        // Show the account picker and get the credential
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context as Activity
                        )

                        // Extract the Google ID token from the credential
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(result.credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        Timber.d("Google ID token obtained successfully")

                        // Pass the token to the ViewModel for Firebase sign-in
                        viewModel.signInWithGoogleIdToken(idToken)

                    } catch (e: GetCredentialCancellationException) {
                        Timber.d("Google Sign-In cancelled by user")
                        viewModel.onGoogleSignInFailed("Sign-in cancelled")
                    } catch (e: Exception) {
                        Timber.e(e, "Google Sign-In failed")
                        viewModel.onGoogleSignInFailed(e.message ?: "Google Sign-In failed")
                    }
                }
            }
        )
    }
}

@Composable
private fun SignInContent(
    modifier: Modifier = Modifier,
    uiState: UiState<Boolean>,
    onSignInClick: (String, String) -> Unit,
    onSignUpClick: (String, String) -> Unit,
    onForgotPasswordClick: (String, (Boolean) -> Unit) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var loadingSource by remember { mutableStateOf<String?>(null) }
    
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val view = androidx.compose.ui.platform.LocalView.current
    
    val isLoading = uiState is UiState.Loading

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            loadingSource = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Branding Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp, bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_brand_logo),
                    contentDescription = "Brand Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                // Sparkle Indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.app_brand_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.app_brand_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Authentication Card
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (isSignUp) "Create an account" else stringResource(id = R.string.sign_in_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isSignUp) "Sign up to track your applications" else stringResource(id = R.string.sign_in_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                AppSecondaryButton(
                    text = stringResource(id = R.string.continue_with_google),
                    onClick = {
                        loadingSource = "google"
                        onGoogleSignInClick()
                    },
                    isLoading = isLoading && loadingSource == "google",
                    textColor = Color.Black,
                    containerColor = Color.White,
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = stringResource(id = R.string.or_continue_with_email).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    label = stringResource(id = R.string.email_label),
                    placeholder = stringResource(id = R.string.email_placeholder),
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    label = stringResource(id = R.string.password_label),
                    placeholder = stringResource(id = R.string.password_placeholder),
                    isPassword = true,
                    errorMessage = passwordError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                
                if (isSignUp) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPasswordError = null },
                        label = "Confirm Password",
                        placeholder = "Confirm your password",
                        isPassword = true,
                        errorMessage = confirmPasswordError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                if (!isSignUp) {
                    Text(
                        text = stringResource(id = R.string.forgot_password),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { showForgotPasswordDialog = true }
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AppButton(
                    text = if (isSignUp) "Sign Up" else stringResource(id = R.string.sign_in_button),
                    onClick = {
                        focusManager.clearFocus()
                        context.hideKeyboard(view)
                        
                        var hasError = false
                        if (email.isBlank()) {
                            emailError = "Email is required"
                            hasError = true
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Invalid email format"
                            hasError = true
                        }
                        
                        if (password.isBlank()) {
                            passwordError = "Password is required"
                            hasError = true
                        }
                        
                        if (isSignUp) {
                            val hasMinLength = password.length >= 7
                            val hasUpper = password.any { it.isUpperCase() }
                            val hasLower = password.any { it.isLowerCase() }
                            val hasDigit = password.any { it.isDigit() }
                            val hasSpecial = password.any { !it.isLetterOrDigit() }

                            if (password.isNotBlank() && (!hasMinLength || !hasUpper || !hasLower || !hasDigit || !hasSpecial)) {
                                passwordError = "Min 7 chars, 1 uppercase, 1 lowercase, 1 number, 1 special character"
                                hasError = true
                            }

                            if (password != confirmPassword) {
                                confirmPasswordError = "Passwords do not match"
                                hasError = true
                            }
                        }

                        if (hasError) return@AppButton

                        loadingSource = "email"
                        if (isSignUp) {
                            onSignUpClick(email, password)
                        } else {
                            onSignInClick(email, password)
                        }
                    },
                    isLoading = isLoading && loadingSource == "email"
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Bottom Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            Row {
                Text(
                    text = if (isSignUp) "Already have an account? " else stringResource(id = R.string.dont_have_account) + " ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isSignUp) "Sign In" else stringResource(id = R.string.sign_up),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { 
                        isSignUp = !isSignUp 
                        email = ""
                        password = ""
                        confirmPassword = ""
                        emailError = null
                        passwordError = null
                        confirmPasswordError = null
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.terms_and_privacy),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }

    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf("") }
        var resetEmailError by remember { mutableStateOf<String?>(null) }
        var isResetting by remember { mutableStateOf(false) }
        
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { if (!isResetting) showForgotPasswordDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email address to receive a password reset link.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    AppTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it; resetEmailError = null },
                        label = "Email",
                        placeholder = "your@email.com",
                        errorMessage = resetEmailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
            },
            confirmButton = {
                val dialogFocusManager = androidx.compose.ui.platform.LocalFocusManager.current
                val dialogKeyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
                androidx.compose.material3.TextButton(
                    onClick = {
                        dialogFocusManager.clearFocus()
                        dialogKeyboardController?.hide()
                        
                        if (resetEmail.isBlank()) {
                            resetEmailError = "Email is required"
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
                            resetEmailError = "Invalid email format"
                        } else {
                            isResetting = true
                            onForgotPasswordClick(resetEmail) { success ->
                                isResetting = false
                                if (success) {
                                    showForgotPasswordDialog = false
                                }
                            }
                        }
                    },
                    enabled = !isResetting
                ) {
                    if (isResetting) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Send Link")
                    }
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showForgotPasswordDialog = false },
                    enabled = !isResetting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
