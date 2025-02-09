package dev.tswanson.fetchquest.android.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.tswanson.fetchquest.android.Event
import dev.tswanson.fetchquest.android.Registration

@Composable
fun QuestDetailView(event: Event, attendees: List<Registration>) {
    Column {
        Text(event.title)
        Text(event.description)
        Text("${event.hours} hours")
        Text(event.createdBy?.name ?: "Unknown Organization")
        Text("Attendees")
        LazyColumn {
            items(attendees) { reg ->
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

private fun sendMail(context: Context, email: String, name: String) {
    val i = Intent(Intent.ACTION_SEND)
    i.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    i.setType("message/rfc822")
    context.startActivity(Intent.createChooser(i,"Send a message to $name"))
}