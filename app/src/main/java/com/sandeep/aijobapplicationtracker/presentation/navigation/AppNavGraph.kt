package com.sandeep.aijobapplicationtracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sandeep.aijobapplicationtracker.presentation.screens.splash.SplashScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.home.HomeScreen

import com.sandeep.aijobapplicationtracker.presentation.screens.signin.SignInScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.careersetup.CareerSetupScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.applications.ApplicationsListScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.addapplication.AddApplicationScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.applicationdetail.ApplicationDetailScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.addinterview.AddInterviewScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.aijobanalyzer.AiJobAnalyzerScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.aianalysisresult.AiAnalysisResultScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.myresumes.MyResumesScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.airesumematch.AiResumeMatchScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.aiassistant.AiAssistantScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.aiinterviewprep.AiInterviewPrepScreen
import com.sandeep.aijobapplicationtracker.presentation.screens.profilesettings.ProfileSettingsScreen

/**
 * Main navigation graph for the application.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.CareerSetup.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.CareerSetup.route) {
            CareerSetupScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CareerSetup.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToApplications = { navController.navigate(Screen.ApplicationsList.route) },
                onNavigateToProfile = { navController.navigate(Screen.ProfileSettings.route) },
                onNavigateToAssistant = { navController.navigate(Screen.AiAssistant.route) }
            )
        }
        
        composable(Screen.ApplicationsList.route) {
            ApplicationsListScreen(
                onNavigateToAddJob = { navController.navigate(Screen.AddApplication.route) },
                onNavigateToDetail = { jobId -> navController.navigate(Screen.ApplicationDetail.createRoute(jobId)) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToAssistant = { navController.navigate(Screen.AiAssistant.route) },
                onNavigateToProfile = { navController.navigate(Screen.ProfileSettings.route) }
            )
        }
        
        composable(Screen.AddApplication.route) {
            AddApplicationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAnalyzer = { navController.navigate(Screen.AiJobAnalyzer.route) }
            )
        }
        
        composable(Screen.ApplicationDetail.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            ApplicationDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddInterview = { navController.navigate(Screen.AddInterview.createRoute(jobId)) },
                onNavigateToResumeMatch = { navController.navigate(Screen.AiResumeMatch.createRoute(jobId)) }
            )
        }
        
        composable(Screen.AddInterview.route) {
            AddInterviewScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AiJobAnalyzer.route) {
            AiJobAnalyzerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { navController.navigate(Screen.AiAnalysisResult.route) }
            )
        }
        
        composable(Screen.AiAnalysisResult.route) {
            AiAnalysisResultScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.ApplicationsList.route) {
                        popUpTo(Screen.ApplicationsList.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.MyResumes.route) {
            MyResumesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AiResumeMatch.route) {
            AiResumeMatchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AiAssistant.route) {
            AiAssistantScreen(
                onNavigateToPrep = { jobId -> navController.navigate(Screen.AiInterviewPrep.createRoute(jobId)) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToApplications = { navController.navigate(Screen.ApplicationsList.route) },
                onNavigateToProfile = { navController.navigate(Screen.ProfileSettings.route) }
            )
        }
        
        composable(Screen.AiInterviewPrep.route) {
            AiInterviewPrepScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ProfileSettings.route) {
            ProfileSettingsScreen(
                onNavigateToResumes = { navController.navigate(Screen.MyResumes.route) },
                onLogout = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToApplications = { navController.navigate(Screen.ApplicationsList.route) },
                onNavigateToAssistant = { navController.navigate(Screen.AiAssistant.route) }
            )
        }
    }
}
