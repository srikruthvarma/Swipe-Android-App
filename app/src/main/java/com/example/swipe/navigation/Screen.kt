package com.example.swipe.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Products : Screen("products", "Products", Icons.Default.List)
    object Uploads : Screen("uploads", "My Uploads", Icons.Default.Upload)
}