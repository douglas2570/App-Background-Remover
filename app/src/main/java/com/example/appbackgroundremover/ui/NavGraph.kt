package com.example.appbackgroundremover.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appbackgroundremover.data.local.ImageManager
import com.example.appbackgroundremover.ui.navigation.Screen
import com.example.appbackgroundremover.ui.screens.HomeScreen
import com.example.appbackgroundremover.ui.screens.RemoveBackgroundScreen
import com.example.appbackgroundremover.ui.screens.SaveResultScreen
import com.example.appbackgroundremover.ui.viewmodel.HomeViewModel
import com.example.appbackgroundremover.ui.viewmodel.HomeViewModelFactory


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val imageManager = ImageManager(context)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(imageManager)
            )
            HomeScreen(navController = navController, viewModel = homeViewModel)
        }

        composable(Screen.RemoveBackground.route) {
            RemoveBackgroundScreen(navController)
        }

        composable(Screen.SaveResult.route) {
            SaveResultScreen(navController, null)
        }

        composable(
            route = "save_result/{filePath}",
            arguments = listOf(androidx.navigation.navArgument("filePath") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath")

            SaveResultScreen(
                navController = navController,
                filePathArg = filePath
            )
        }
    }
}