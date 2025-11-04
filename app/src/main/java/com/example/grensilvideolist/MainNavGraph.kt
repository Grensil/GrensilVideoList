package com.example.grensilvideolist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookmark.BookmarkScreen
import com.example.designsystem.theme.PurpleGrey40
import com.example.designsystem.theme.PurpleGrey80
import com.example.main.HomeScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                )
        ) {
            MainNavGraph(
                navController = navController
            )
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Home tab
        val isHomeSelected = currentRoute == Routes.Home.path
        Image(
            imageVector = if (isHomeSelected) Icons.AutoMirrored.Filled.List else Icons.AutoMirrored.Outlined.List,
            contentDescription = "list icon",
            colorFilter = ColorFilter.tint(if (isHomeSelected) PurpleGrey40 else PurpleGrey80),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    navController.navigate(Routes.Home.path) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
        )

        // Bookmark tab
        val isBookmarkSelected = currentRoute == Routes.Bookmark.path
        Image(
            imageVector = if (isBookmarkSelected) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "bookmark icon",
            colorFilter = ColorFilter.tint(if (isBookmarkSelected) PurpleGrey40 else PurpleGrey80),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    navController.navigate(Routes.Bookmark.path) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
        )
    }
}

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home.path) {
        composable(route = Routes.Home.path) { backStackEntry ->
            HomeScreen(navController = navController)
        }
        composable(route = Routes.Bookmark.path) { backStackEntry ->
            BookmarkScreen(navController = navController)
        }
    }
}