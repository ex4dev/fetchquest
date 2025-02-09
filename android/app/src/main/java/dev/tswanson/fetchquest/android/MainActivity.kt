package dev.tswanson.fetchquest.android

import SignInPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.tswanson.fetchquest.android.model.QuestInfoViewModel
import dev.tswanson.fetchquest.android.ui.AppSearchBar
import dev.tswanson.fetchquest.android.ui.ExplorePage
import dev.tswanson.fetchquest.android.ui.QuestDetailView
import dev.tswanson.fetchquest.android.ui.QuestsPage
import dev.tswanson.fetchquest.android.ui.StatsPage
import dev.tswanson.fetchquest.android.ui.theme.FetchQuestTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            FetchQuestTheme {
                Scaffold(
                    topBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth().clipToBounds()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wood),
                                contentDescription = "Wood",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .scale(6f)
                                    .height(64.dp)
                                    .fillMaxWidth()
                                    .clipToBounds()
                            )
                            Row {
                                AppSearchBar(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                                )
                            }
                        }
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth().clipToBounds()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wood),
                                contentDescription = "Wood",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .scale(2f)
                                    .height(83.dp)
                            )
                            BottomAppBar(
                                containerColor = Color.Transparent,
                                actions = {

                                    NavigationBarItem(
                                        selected = false,
                                        onClick = { navController.navigate("explore") },
                                        icon = {
                                            Icon(
                                                Icons.Filled.Place,
                                                contentDescription = "Explore",
                                                tint = Color.White
                                            )
                                        },
                                        label = { Text("Explore", color = Color.White) }
                                    )
                                    NavigationBarItem(
                                        selected = false,
                                        onClick = { navController.navigate("quests") },
                                        icon = {
                                            Icon(
                                                Icons.Filled.Info,
                                                contentDescription = "Quests",
                                                tint = Color.White
                                            )
                                        },
                                        label = { Text("Quests", color = Color.White) }
                                    )
                                    NavigationBarItem(
                                        selected = false,
                                        onClick = { navController.navigate("stats") },
                                        icon = {
                                            Icon(
                                                Icons.Filled.Menu,
                                                contentDescription = "Stats",
                                                tint = Color.White
                                            )
                                        },
                                        label = { Text("Stats", color = Color.White) }
                                    )
                                })
                        }
                    },
                    modifier = Modifier.fillMaxSize().background(Color.Transparent))
                { innerPadding ->
                    val questInfoViewModel = viewModel { QuestInfoViewModel() }
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    if (questInfoViewModel.sheetVisible.collectAsState().value) {
                        ModalBottomSheet(
                            sheetState = sheetState,
                            onDismissRequest = { questInfoViewModel.setVisible(false) }
                        ) {
                            val e = questInfoViewModel.currentEvent.collectAsState()
                            val a = questInfoViewModel.currentEventAttendees.collectAsState()
                            val event = e.value ?: return@ModalBottomSheet
                            val attendees = a.value ?: listOf()
                            QuestDetailView(event, attendees)
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.scroll),
                        contentDescription = "Scroll",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .scale(2.1f)
                    )
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        questInfoViewModel = questInfoViewModel,
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
    questInfoViewModel: QuestInfoViewModel,
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
            QuestsPage(questInfoViewModel = questInfoViewModel)
        }
        composable(route = "stats") {
            StatsPage(1, 2, 3,4, 6)
        }
        composable(route = "sign-in") {
            SignInPage()
        }
    }
}