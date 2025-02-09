package dev.tswanson.fetchquest.android.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tswanson.fetchquest.android.APIConnection
import dev.tswanson.fetchquest.android.Event
import kotlinx.coroutines.launch

class QuestListViewModel : ViewModel() {
    private val _quests = mutableStateListOf<Event>()
    val quests: List<Event> = _quests

    private val _myQuests = mutableStateListOf<Event>()
    val myQuests: List<Event> = _myQuests

    fun addMyQuest(event: Event) {
        _myQuests.add(event)
        viewModelScope.launch {
            APIConnection.instance.retrofitService.joinEvent(event.id)
        }
    }

    fun removeMyQuest(event: Event) {
        _myQuests.remove(event)
        viewModelScope.launch {
            APIConnection.instance.retrofitService.leaveEvent(event.id)
        }
    }

    fun fetchQuests() {
        viewModelScope.launch {
            _quests.clear()
            _quests.addAll(APIConnection.instance.retrofitService.getEvents())

            _myQuests.clear()
            _myQuests.addAll(APIConnection.instance.retrofitService.getMyEvents())
        }
    }
}