package com.example.noms.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.noms.R

@Composable
fun TopBarComponent(
    //onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.noms_logo),
            contentDescription = "Noms Logo",
            modifier = Modifier.height(28.dp),
            contentScale = ContentScale.Fit
        )

        // Noti
        /*
        Box {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
            // Yellow dot notification badge
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.TopEnd)
            )
        }*/
    }
}
