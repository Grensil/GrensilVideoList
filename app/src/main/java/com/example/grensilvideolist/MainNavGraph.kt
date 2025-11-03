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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookmark.BookmarkScreen
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

    Row(modifier = Modifier.fillMaxWidth().height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

        Image(imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = "list icon",
            modifier = Modifier.weight(1f).clickable {
                navController.navigate(Routes.Home.path) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })

        Image(imageVector = Icons.Default.Favorite,
            contentDescription = "bookmark icon",
            modifier = Modifier.weight(1f).clickable {
                navController.navigate(Routes.Bookmark.path) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
    }
}

@Composable
fun MainNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController, startDestination = Routes.Home.path
    ) {
        composable(route = Routes.Home.path) { backStackEntry ->
            HomeScreen(navController = navController)
        }
        composable(route = Routes.Bookmark.path) { backStackEntry ->
            BookmarkScreen(navController = navController)
        }
    }
}