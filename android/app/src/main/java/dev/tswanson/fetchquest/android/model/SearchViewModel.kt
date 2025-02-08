package dev.tswanson.fetchquest.android.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _active = MutableStateFlow(false)
    val active = _active.asStateFlow()

    fun onQueryChange(text: String) {
        _query.value = text
        _active.value = (text.isNotEmpty())
    }

    fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onQueryChange("")
        }
    }
}