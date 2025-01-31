package org.turter.patrocl.presentation.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.turter.patrocl.presentation.auth.AuthUiEvent

@Composable
fun LoginPage(onLogin: () -> Unit) {
    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(end = 30.dp),
                text = "TurterApp",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
            Text(
                modifier = Modifier.padding(start = 14.dp),
                text = "Blue `da Nos",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onLogin
            ) {
                Text(
                    text = "Войти".uppercase()
                )
            }
        }
    }
}