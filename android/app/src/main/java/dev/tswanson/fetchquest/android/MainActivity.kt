package dev.tswanson.fetchquest.android

import SignInPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.tswanson.fetchquest.android.ui.AppSearchBar
import dev.tswanson.fetchquest.android.ui.ExplorePage
import dev.tswanson.fetchquest.android.ui.QuestsPage
import dev.tswanson.fetchquest.android.ui.StatsPage
import dev.tswanson.fetchquest.android.ui.theme.FetchQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            FetchQuestTheme {
                Scaffold(
                    topBar = {
                        Row {
                            AppSearchBar(
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            )
                        }
                    },
                    bottomBar = {
                        BottomAppBar(actions = {
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate("explore") },
                                icon = { Icon(Icons.Filled.Place, contentDescription = "Explore") },
                                label = { Text("Explore") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate("quests") },
                                icon = { Icon(Icons.Filled.Info, contentDescription = "Quests") },
                                label = { Text("Quests") }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { navController.navigate("stats") },
                                icon = { Icon(Icons.Filled.Menu, contentDescription = "Stats") },
                                label = { Text("Stats") }
                            )
                        })
                    },
                    modifier = Modifier.fillMaxSize())
                { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = "sign-in",
        modifier = modifier,
    ) {
        composable(route = "explore") {
            ExplorePage()
        }
        composable(route = "quests") {
            QuestsPage()
        }
        composable(route = "stats") {
            StatsPage()
        }
        composable(route = "sign-in") {
            SignInPage()
        }
    }
}