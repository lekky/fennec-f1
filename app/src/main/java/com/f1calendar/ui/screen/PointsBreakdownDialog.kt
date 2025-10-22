package com.f1calendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f1calendar.data.model.Driver

data class RacePoints(
    val raceName: String,
    val round: Int,
    val position: String?,
    val points: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverPointsBreakdownDialog(
    driver: Driver,
    teamColor: Color,
    racePoints: List<RacePoints>,
    totalPoints: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                TopAppBar(
                    title = {
                        Column {
                            Text("${driver.givenName} ${driver.familyName}")
                            Text(
                                text = "Total: $totalPoints points",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = teamColor,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )

                // Points breakdown
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(racePoints) { racePoint ->
                        RacePointCard(racePoint = racePoint, teamColor = teamColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun RacePointCard(
    racePoint: RacePoints,
    teamColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team color bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(teamColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Race info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = racePoint.raceName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Round ${racePoint.round}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Position
            racePoint.position?.let { pos ->
                Text(
                    text = when (pos) {
                        "1" -> "🥇"
                        "2" -> "🥈"
                        "3" -> "🥉"
                        else -> "P$pos"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Points
            Text(
                text = if (racePoint.points == "0") "-" else "+${racePoint.points}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (racePoint.points == "0")
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                else
                    teamColor
            )
        }
    }
}
