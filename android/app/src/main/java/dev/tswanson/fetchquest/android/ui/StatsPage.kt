package dev.tswanson.fetchquest.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatsPage(pastWeek: Int, pastMonth: Int, pastYear: Int,
              allTime: Int, streak: Int) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(text = "Player Stats", style = MaterialTheme.typography.headlineMedium)
        StatItem("Hours in the past week", pastWeek)
        StatItem("Hours in the past month", pastMonth)
        StatItem("Hours in the past year", pastYear)
        StatItem("All time hours", allTime)
        StatItem("Current streak", streak)

    }

}

@Composable
fun StatItem(title: String, value: Int) {
    val customColor = Color(0xFFD2B48C)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            colors = CardDefaults.cardColors(containerColor = customColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Fill the available width
                    .padding(8.dp), // Padding around the row inside the card
                horizontalArrangement = Arrangement.SpaceBetween, // Space between the text elements
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (title.equals("Current streak")) {
                    if (value > 2) {
                        val titleStreak = title + " \uD83D\uDD25"
                        Text (
                            text = titleStreak, modifier = Modifier
                                .padding(9.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = value.toString(), modifier = Modifier
                                .padding(9.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else {
                        Text(
                            text = title, modifier = Modifier
                                .padding(9.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = value.toString(), modifier = Modifier
                                .padding(9.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                }
                else {
                    Text(
                        text = title, modifier = Modifier
                            .padding(9.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = value.toString(), modifier = Modifier
                            .padding(9.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

}