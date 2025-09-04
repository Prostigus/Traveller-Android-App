package com.divine.traveller.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.divine.traveller.R

@Composable
fun ThemeToggle(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(if (isDarkTheme) R.drawable.filled_dark_mode else R.drawable.outline_dark_mode),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (isDarkTheme) "Dark Theme" else "Light Theme",
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Switch(
            checked = isDarkTheme,
            onCheckedChange = onThemeToggle
        )
    }
}