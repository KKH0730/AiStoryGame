package seno.st.aistorygame.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import seno.st.aistorygame.ui.HomeScreen
import seno.st.aistorygame.ui.MainScreen

enum class NavigationRoute(val routeName: String, val icon: Int?) {
    MAIN("MAIN", null),
//    LOGIN("LOGIN", null),
    HOME("HOME", null)
//    HOME2("HOME2", null),
//    HOME3("HOME3", null),
//    PROFILE("PROFILE", null),
//    SPLASH("SPLASH", null),
}

@Composable
fun NavigationGraph(startRoute: NavigationRoute = NavigationRoute.HOME, navController: NavHostController) {
    val routeAction = remember(navController) { RouteAction(navController) }

    NavHost(navController = navController, startDestination = startRoute.routeName) {
        composable(NavigationRoute.MAIN.routeName) {
            MainScreen()
        }
        composable(NavigationRoute.HOME.routeName) {
            HomeScreen()
        }

//        composable(NavigationRoute.HOME2.routeName) {
//
//        }
//
//        composable(NavigationRoute.HOME3.routeName) {
//
//        }
//
//        composable(NavigationRoute.PROFILE.routeName) {
//
//        }
    }
}