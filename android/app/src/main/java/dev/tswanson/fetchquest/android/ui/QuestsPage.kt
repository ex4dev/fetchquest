package dev.tswanson.fetchquest.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tswanson.fetchquest.android.Event
import dev.tswanson.fetchquest.android.model.QuestInfoViewModel
import dev.tswanson.fetchquest.android.model.QuestListViewModel

@Composable
fun QuestsPage(
    questInfoViewModel: QuestInfoViewModel
) {
    val viewModel = viewModel { QuestListViewModel() }
    val allQuestList = viewModel.quests
    val myQuestList = viewModel.myQuests

    LaunchedEffect(Unit) { viewModel.fetchQuests() }

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        VerticalScrollingList(viewModel, questInfoViewModel, myQuestList, "My Quests")
        VerticalScrollingList(viewModel, questInfoViewModel, allQuestList, "All Quests")
    }
}

@Composable
fun VerticalScrollingList(
    viewModel: QuestListViewModel,
    questInfoViewModel: QuestInfoViewModel,
    list: List<Event>,
    title: String,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(16.dp)
    ) {
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
        items(list) { item ->
            QuestCard(item, viewModel) {
                questInfoViewModel.setVisible(true)
                questInfoViewModel.setCurrentEvent(item)
            }
        }
    }
}