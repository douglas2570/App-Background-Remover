package com.example.appbackgroundremover.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RemoveBackground : Screen("remove_background")
    object SaveResult : Screen("save_result/{filePath}") {
        fun createRoute(filePath: String) = "save_result/$filePath"
    }
}