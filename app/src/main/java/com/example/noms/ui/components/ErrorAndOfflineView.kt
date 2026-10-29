package com.example.noms.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineCacheBanner(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "Offline indicator",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Offline Mode",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }


            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onRefresh() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Refresh",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Try Again", fontSize = 14.sp)
                }
            }
        }
    }
}
