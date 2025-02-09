package dev.tswanson.fetchquest.android.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tswanson.fetchquest.android.APIConnection
import dev.tswanson.fetchquest.android.Event
import dev.tswanson.fetchquest.android.Registration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestInfoViewModel : ViewModel() {
    private val _sheetVisible = MutableStateFlow(false)
    val sheetVisible = _sheetVisible.asStateFlow()

    private val _currentEvent = MutableStateFlow<Event?>(null)
    val currentEvent = _currentEvent.asStateFlow()

    private val _currentEventAttendees = MutableStateFlow<List<Registration>?>(null)
    val currentEventAttendees = _currentEventAttendees.asStateFlow()

    fun setVisible(sheetVisible: Boolean) {
        _sheetVisible.value = sheetVisible
    }

    fun setCurrentEvent(event: Event) {
        _currentEvent.value = event
        viewModelScope.launch {
            _currentEventAttendees.value =
                APIConnection.instance.retrofitService.getAttendees(event.id)
        }
    }
}