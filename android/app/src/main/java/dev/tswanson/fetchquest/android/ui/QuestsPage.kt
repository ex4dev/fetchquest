package dev.tswanson.fetchquest.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuestsPage() {
    val q1: Quest = Quest("Help at the Food Bank", "Assist in sorting and distributing food.", "50",2,1,"");
    val q2: Quest = Quest("Tutor a Student", "Provide online tutoring for a student in need.", "100",4,5,"");
    val q3: Quest = Quest("Elderly Assistance", "Help an elderly person with daily tasks.", "75",7,1,"");

    val itemList = listOf(q1,q2,q3);
    VerticalScrollingList(itemList);
}

@Composable
fun VerticalScrollingList(items: List<Quest>) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        items.forEach { item ->
            val text : String = item.name;
            Text(
                text = text,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}