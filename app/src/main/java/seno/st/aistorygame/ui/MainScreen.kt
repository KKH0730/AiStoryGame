package seno.st.aistorygame.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import seno.st.aistorygame.navigation.NavigationGraph

@Composable
fun MainScreen() {
    val navController = rememberNavController()

//    Scaffold(
//        bottomBar = { BottomNavigationBar(navController = navController) },
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
//            NavigationGraph(navController = navController)
//        }
//    }


    Box {
        NavigationGraph(navController = navController)
    }
}