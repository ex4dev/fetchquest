package dev.tswanson.fetchquest.android.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.tswanson.fetchquest.android.Event
import dev.tswanson.fetchquest.android.Registration

@Composable
fun QuestDetailView(event: Event, attendees: List<Registration>) {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Text(event.title, style = MaterialTheme.typography.headlineLarge)
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(0.dp, 6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.Edit, "Description")
                Text(event.description)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.DateRange, "Time")
                Text("${event.hours} hours")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.Person, "Person")
                Text(event.createdBy?.name ?: "Unknown Organization")
            }
        }
        Text("Attendees", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(attendees) { reg ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(reg.user.name)
                    val context = LocalContext.current
                    IconButton(onClick = {
                        sendMail(context, reg.user.email, reg.user.name)
                    }) {
                        Icon(Icons.Filled.Email, "Email")
                    }
                }
            }
        }
    }
}

private fun sendMail(context: Context, email: String, name: String) {
    val i = Intent(Intent.ACTION_SEND)
    i.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    i.setType("message/rfc822")
    context.startActivity(Intent.createChooser(i,"Send a message to $name"))
}