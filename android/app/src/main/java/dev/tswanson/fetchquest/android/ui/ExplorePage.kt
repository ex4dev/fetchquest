package dev.tswanson.fetchquest.android.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import dev.tswanson.fetchquest.android.APIConnection
import dev.tswanson.fetchquest.android.MapPointInfoWindow
import dev.tswanson.fetchquest.android.model.QuestInfoViewModel
import dev.tswanson.fetchquest.android.model.QuestListViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ExplorePage(questInfoViewModel: QuestInfoViewModel, navController: NavHostController) {

    val viewModel = viewModel { QuestListViewModel() }
    val quests = viewModel.quests

    if (!APIConnection.isInitialized()) {
        return
        // TODO redirect to the sign-in page or show a dialog or something
    }
    LaunchedEffect(Unit) { viewModel.fetchQuests() }

    var geoPoint = remember { mutableStateOf(GeoPoint(35.7841832, -78.6648615)) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

            }
        },
        update = { view ->
            Configuration.getInstance().userAgentValue = context.packageName
            view.controller.setCenter(geoPoint.value)
            view.controller.setZoom(16.0)

            view.overlays.clear()
            for (quest in quests) {
                val marker = Marker(view)
                marker.position = GeoPoint(quest.lat.toDouble(), quest.long.toDouble())
                marker.infoWindow = MapPointInfoWindow(view, quest) {
                    navController.navigate("quests")
                    questInfoViewModel.setCurrentEvent(quest)
                    questInfoViewModel.setVisible(true)
                }
                view.overlays.add(marker)
            }
        }
    )
}