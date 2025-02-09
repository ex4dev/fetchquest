import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dev.tswanson.fetchquest.android.APIConnection
import dev.tswanson.fetchquest.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignInPage(afterSignIn: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "FetchQuest",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
                )
            Text("Sign in to start questing!")
            Button(onClick = {
                signIn(context, scope, afterSignIn)
            }) {
                Text("Sign in with Google")
            }
        }
    }
}

fun signIn(context: Context, coroutineScope: CoroutineScope, afterSignIn: () -> Unit) {
    val credentialManager = CredentialManager.create(context)
    val signinGoogleOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(context.getString(R.string.gcp_id))
        .setAutoSelectEnabled(true) // automatically sign in the user after the first time
        .build()

    val getCredRequest = GetCredentialRequest(
        listOf(signinGoogleOption),
    )

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                context = context,
                request = getCredRequest
            )
            handleSignIn(result)
            afterSignIn()
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            Toast.makeText(context, e.errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}

suspend fun handleSignIn(result: GetCredentialResponse) {
    var credential = result.credential
    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    credential = GoogleIdTokenCredential.createFrom(credential.data)
                    APIConnection.instance = APIConnection(credential.idToken)
                    credential.idToken
                    Log.i("ABC", APIConnection.instance.retrofitService.getUserInfo().toString())
                } catch (e: GoogleIdTokenParsingException) {
                    // TODO handle
                    Log.i("ABC", "ERRROR")
                }
            }
        }
    }
}