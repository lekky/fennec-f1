package com.f1calendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f1calendar.data.model.DriverStanding
import com.f1calendar.ui.theme.F1Gold
import com.f1calendar.ui.viewmodel.DriverStandingsUiState
import com.f1calendar.ui.viewmodel.DriverStandingsViewModel
import com.f1calendar.util.TeamColors

@Composable
fun DriverStandingsScreen(
    viewModel: DriverStandingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDriver by remember { mutableStateOf<DriverStanding?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is DriverStandingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DriverStandingsUiState.Success -> {
                DriverStandingsList(
                    standings = state.standings,
                    onDriverClick = { selectedDriver = it }
                )
            }

            is DriverStandingsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${state.message}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }

    // Show points breakdown dialog
    selectedDriver?.let { driver ->
        val teamColor = driver.constructors.firstOrNull()?.let {
            TeamColors.getTeamColor(it.constructorId)
        } ?: Color.Gray

        // Create mock points breakdown (you could fetch real data here)
        val racePoints = createMockPointsBreakdown(driver.points.toIntOrNull() ?: 0)

        DriverPointsBreakdownDialog(
            driver = driver.driver,
            teamColor = teamColor,
            racePoints = racePoints,
            totalPoints = driver.points,
            onDismiss = { selectedDriver = null }
        )
    }
}

// Helper function to create sample breakdown
private fun createMockPointsBreakdown(totalPoints: Int): List<RacePoints> {
    val races = listOf(
        "Bahrain GP", "Saudi Arabian GP", "Australian GP", "Japanese GP",
        "Chinese GP", "Miami GP", "Emilia Romagna GP", "Monaco GP"
    )

    return races.mapIndexed { index, raceName ->
        val points = if (totalPoints > 0 && index < 5) {
            (totalPoints / 5).coerceAtMost(25)
        } else 0

        RacePoints(
            raceName = raceName,
            round = index + 1,
            position = if (points > 0) ((1..10).random().toString()) else null,
            points = points.toString()
        )
    }
}

@Composable
private fun DriverStandingsList(
    standings: List<DriverStanding>,
    onDriverClick: (DriverStanding) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🏆 DRIVERS CHAMPIONSHIP",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        itemsIndexed(standings) { index, standing ->
            DriverStandingCard(
                standing = standing,
                position = index + 1,
                leaderPoints = standings.firstOrNull()?.points?.toDoubleOrNull() ?: 0.0,
                onClick = { onDriverClick(standing) }
            )
        }
    }
}

@Composable
private fun DriverStandingCard(
    standing: DriverStanding,
    position: Int,
    leaderPoints: Double,
    onClick: () -> Unit
) {
    val teamColor = standing.constructors.firstOrNull()?.let {
        TeamColors.getTeamColor(it.constructorId)
    } ?: Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position indicator with team color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(teamColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Position number
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (position) {
                            1 -> F1Gold
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = position.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (position <= 3) Color.Black else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Driver info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    standing.driver.permanentNumber?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = teamColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "${standing.driver.givenName} ${standing.driver.familyName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                standing.constructors.firstOrNull()?.let { constructor ->
                    Text(
                        text = constructor.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Points
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = standing.points,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                if (position > 1) {
                    val pointsDiff = leaderPoints - (standing.points.toDoubleOrNull() ?: 0.0)
                    Text(
                        text = "(-${pointsDiff.toInt()})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
