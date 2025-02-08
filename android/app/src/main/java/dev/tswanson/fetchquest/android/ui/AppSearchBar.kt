package dev.tswanson.fetchquest.android.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.tswanson.fetchquest.android.model.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(
    modifier: Modifier = Modifier
) {
    val searchViewModel = viewModel { SearchViewModel() }

    val searchQuery = searchViewModel.query.collectAsState()
    val searchBarActive = searchViewModel.active.collectAsState()

    SearchBar(
        query = searchQuery.value,
        onQueryChange = searchViewModel::onQueryChange,
        onSearch = searchViewModel::onQueryChange,
        active = searchBarActive.value,
        onActiveChange = { searchViewModel.onToggleSearch() },
        placeholder = { Text("Search for quests or places") },
        modifier = modifier,
    ) {
        LazyColumn {
            item {
                Text("Result 2")
            }
            item {
                Text("Result 2")
            }
        }
    }
}