package de.hundesportteam.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.hundesportteam.app.data.preferences.PreferencesManager
import de.hundesportteam.app.ui.blog.BlogScreen
import de.hundesportteam.app.ui.blog.BlogViewModel
import de.hundesportteam.app.ui.detail.ContentDetailScreen
import de.hundesportteam.app.ui.pages.PageViewModel
import de.hundesportteam.app.ui.pages.PagesScreen
import de.hundesportteam.app.ui.parcour.ElementTemplateManagerScreen
import de.hundesportteam.app.ui.parcour.ParcourEditorScreen
import de.hundesportteam.app.ui.parcour.ParcourListScreen
import de.hundesportteam.app.ui.training.TrainingScreen
import de.hundesportteam.app.ui.training.TrainingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Blog : Screen("blog", "Blog", Icons.Default.Article)
    object Pages : Screen("pages", "Verein", Icons.Default.Home)
    object Training : Screen("training", "Training", Icons.Default.FitnessCenter)
    object Parcour : Screen("parcour", "Parcour", Icons.Default.Route)
    object ParcourEditor : Screen("parcour_editor?id={id}", "Parcour-Editor", Icons.Default.Route) {
        fun createRoute(id: Long? = null) = if (id != null) "parcour_editor?id=$id" else "parcour_editor"
    }
    object TemplateManager : Screen("template_manager", "Vorlagen", Icons.Default.Route)
    object Detail : Screen("detail/{type}/{id}/{title}", "Detail", Icons.Default.Info) {
        fun createRoute(type: String, id: Int, title: String) = "detail/$type/$id/$title"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    preferencesManager: PreferencesManager
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Blog, Screen.Pages, Screen.Training, Screen.Parcour)

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(navController)) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.secondary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Blog.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Blog.route) {
                BlogScreen(
                    onPostClick = { post ->
                        navController.navigate(
                            Screen.Detail.createRoute("blog", post.id, post.title)
                        )
                    }
                )
            }

            composable(Screen.Pages.route) {
                PagesScreen(
                    onPageClick = { page ->
                        navController.navigate(
                            Screen.Detail.createRoute("page", page.id, page.title)
                        )
                    }
                )
            }

            composable(Screen.Training.route) {
                TrainingScreen(
                    onPageClick = { page ->
                        navController.navigate(
                            Screen.Detail.createRoute("training", page.id, page.title)
                        )
                    }
                )
            }

            composable(Screen.Parcour.route) {
                ParcourListScreen(
                    onParcourClick = { id ->
                        navController.navigate(Screen.ParcourEditor.createRoute(id))
                    },
                    onCreateNewClick = {
                        navController.navigate(Screen.ParcourEditor.createRoute())
                    }
                )
            }

            composable(
                route = Screen.ParcourEditor.route,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val rawId = backStackEntry.arguments?.getLong("id") ?: -1L
                ParcourEditorScreen(
                    parcourId = if (rawId == -1L) null else rawId,
                    onBackClick = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    onManageTemplatesClick = { navController.navigate(Screen.TemplateManager.route) }
                )
            }

            composable(Screen.TemplateManager.route) {
                ElementTemplateManagerScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("type") { type = NavType.StringType },
                    navArgument("id") { type = NavType.IntType },
                    navArgument("title") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: ""
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val title = backStackEntry.arguments?.getString("title") ?: ""

                DetailScreenWrapper(
                    type = type,
                    id = id,
                    title = title,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun shouldShowBottomBar(navController: NavHostController): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    return currentRoute in listOf(Screen.Blog.route, Screen.Pages.route, Screen.Training.route, Screen.Parcour.route)
}

@Composable
fun DetailScreenWrapper(
    type: String,
    id: Int,
    title: String,
    onBackClick: () -> Unit
) {
    val trainingViewModel: TrainingViewModel = hiltViewModel()
    val pageViewModel: PageViewModel = hiltViewModel()
    val blogViewModel: BlogViewModel = hiltViewModel()

    var content by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id, type) {
        val result: String? = withContext(Dispatchers.IO) {
            when (type) {
                "training" -> trainingViewModel.getPageById(id)?.content
                "page" -> pageViewModel.getPageById(id)?.content
                "blog" -> blogViewModel.getPostById(id)?.content
                else -> null
            }
        }
        content = result
    }

    if (content != null) {
        ContentDetailScreen(
            title = title,
            content = content!!,
            onBackClick = onBackClick,
        )
    } else {
        ContentDetailScreen(
            title = title,
            content = "<p>Inhalt wird geladen...</p>",
            onBackClick = onBackClick
        )
    }
}
