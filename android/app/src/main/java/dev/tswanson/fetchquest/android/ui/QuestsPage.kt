package dev.tswanson.fetchquest.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuestsPage() {
    val q1: Quest = Quest("Help at the Food Bank", "Assist in sorting and distributing food.", "50",2,1,"");
    val q2: Quest = Quest("Tutor a Student", "Provide online tutoring for a student in need.", "100",4,5,"");
    val q3: Quest = Quest("Elderly Assistance", "Help an elderly person with daily tasks.", "75",7,1,"");
    val q4: Quest = Quest("Help at the Museum", "Assist in sorting and distributing food.", "50",2,1,"");
    val q5: Quest = Quest("Tutor a Elder", "Provide online tutoring for a student in need.", "100",4,5,"");
    val q6: Quest = Quest("Math Assistance", "Help an elderly person with daily tasks.", "75",7,1,"");

    val itemList = listOf(q1,q2,q3,q1,q2,q3,q1);
    val myQuests = listOf(q4,q5,q6,q1,q2,q3,q1,q2,q3,q1);

    Column (
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "My Quests",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        VerticalScrollingList(myQuests, "My Quests")
        Text(
            text = "All Quests",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        VerticalScrollingList(itemList, "All Quests")
    }

}

@Composable
fun VerticalScrollingList(items: List<Quest>, title: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            //.verticalScroll(rememberScrollState())
    ) {
        items.forEachIndexed { index, item ->
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (index < items.size - 1) 12.dp else 0.dp) // Adds spacing between cards
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // Space between title and description

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hours : "+item.reward,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
