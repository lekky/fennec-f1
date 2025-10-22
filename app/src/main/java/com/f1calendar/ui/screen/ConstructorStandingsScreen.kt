package com.f1calendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f1calendar.data.model.Constructor
import com.f1calendar.data.model.ConstructorStanding
import com.f1calendar.ui.theme.F1Gold
import com.f1calendar.ui.viewmodel.ConstructorStandingsUiState
import com.f1calendar.ui.viewmodel.ConstructorStandingsViewModel
import com.f1calendar.util.TeamColors

@Composable
fun ConstructorStandingsScreen(
    viewModel: ConstructorStandingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedConstructor by remember { mutableStateOf<ConstructorStanding?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ConstructorStandingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ConstructorStandingsUiState.Success -> {
                ConstructorStandingsList(
                    standings = state.standings,
                    onConstructorClick = { selectedConstructor = it }
                )
            }

            is ConstructorStandingsUiState.Error -> {
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
    selectedConstructor?.let { constructor ->
        val teamColor = TeamColors.getTeamColor(constructor.constructor.constructorId)
        val racePoints = createMockConstructorPointsBreakdown(constructor.points.toIntOrNull() ?: 0)

        ConstructorPointsBreakdownDialog(
            constructor = constructor.constructor,
            teamColor = teamColor,
            racePoints = racePoints,
            totalPoints = constructor.points,
            onDismiss = { selectedConstructor = null }
        )
    }
}

// Helper function to create sample breakdown
private fun createMockConstructorPointsBreakdown(totalPoints: Int): List<RacePoints> {
    val races = listOf(
        "Bahrain GP", "Saudi Arabian GP", "Australian GP", "Japanese GP",
        "Chinese GP", "Miami GP", "Emilia Romagna GP", "Monaco GP"
    )

    return races.mapIndexed { index, raceName ->
        val points = if (totalPoints > 0 && index < 5) {
            (totalPoints / 5).coerceAtMost(44) // Max 44 points (1-2 finish + fastest lap)
        } else 0

        RacePoints(
            raceName = raceName,
            round = index + 1,
            position = if (points > 0) "1-2" else null, // Show as team result
            points = points.toString()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstructorPointsBreakdownDialog(
    constructor: Constructor,
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
                            Text("🏎️ ${constructor.name}")
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
private fun ConstructorStandingsList(
    standings: List<ConstructorStanding>,
    onConstructorClick: (ConstructorStanding) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🏆 CONSTRUCTORS CHAMPIONSHIP",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        itemsIndexed(standings) { index, standing ->
            ConstructorStandingCard(
                standing = standing,
                position = index + 1,
                leaderPoints = standings.firstOrNull()?.points?.toDoubleOrNull() ?: 0.0,
                onClick = { onConstructorClick(standing) }
            )
        }
    }
}

@Composable
private fun ConstructorStandingCard(
    standing: ConstructorStanding,
    position: Int,
    leaderPoints: Double,
    onClick: () -> Unit
) {
    val teamColor = TeamColors.getTeamColor(standing.constructor.constructorId)

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
            // Team color bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(3.dp))
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

            // Team info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🏎️ ${standing.constructor.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = standing.constructor.nationality,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (standing.wins != "0") {
                    Text(
                        text = "${standing.wins} wins",
                        style = MaterialTheme.typography.bodySmall,
                        color = F1Gold
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
                    if (pointsDiff > 0) {
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
}
