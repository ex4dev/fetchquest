package dev.tswanson.fetchquest.android.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar() {
    SearchBar(
        query = searchText,
        onQueryChange =
    ) { }
    if (searchMode) {
        LazyColumn {

        }
    }
}