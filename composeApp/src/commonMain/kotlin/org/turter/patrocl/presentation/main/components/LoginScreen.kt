package org.turter.patrocl.presentation.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(onLogin: () -> Unit = {}) {

    val primary = MaterialTheme.colorScheme.primaryContainer
//    val lighterPrimary = primary.copy(alpha = 0.8f)

    Surface(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.linearGradient(
//                    colors = listOf(lighterPrimary, primary),
//                    start = Offset(0f, 1000f),
//                    end = Offset(1000f, 0f)
//                )
//            ),
//        containerColor =
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .padding(end = 50.dp)
                    .paddingFromBaseline(bottom = 10.dp),
                text = "TurterApp",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 42.sp,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.8.sp,
            )
            Text(
                modifier = Modifier
                    .padding(start = 50.dp)
                    .paddingFromBaseline(top = 5.dp),
                text = "Blue`da Nos",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 32.sp,
                fontFamily = FontFamily.Serif,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onLogin,
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    text = "ВОЙТИ",
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}