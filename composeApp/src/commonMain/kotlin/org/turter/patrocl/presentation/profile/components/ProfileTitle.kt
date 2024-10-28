package org.turter.patrocl.presentation.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.person.Employee

@Composable
fun ProfileTitle(
    employee: Employee
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Name(
            name = employee.name,
            modifier = Modifier.paddingFromBaseline(36.dp)
        )
        Position(
            employee.position.title,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .paddingFromBaseline(24.dp)
        )
    }
}

@Composable
private fun Name(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun Position(
    positionTitle: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = positionTitle,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}