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
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f1calendar.data.model.DriverStanding
import com.f1calendar.ui.theme.F1Gold
import com.f1calendar.ui.viewmodel.DriverStandingsUiState
import com.f1calendar.ui.viewmodel.DriverStandingsViewModel
import com.f1calendar.util.CountryFlags
import com.f1calendar.util.TeamColors

@Composable
fun DriverStandingsScreen(
    viewModel: DriverStandingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDriver by remember { mutableStateOf<DriverStanding?>(null) }
    var driverResults by remember { mutableStateOf<List<RacePoints>>(emptyList()) }
    var isLoadingResults by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                    onDriverClick = { driver ->
                        selectedDriver = driver
                        isLoadingResults = true
                        coroutineScope.launch {
                            val results = viewModel.getDriverResults(driver.driver.driverId)

                            // Calculate running totals
                            var runningTotal = 0.0
                            driverResults = results.map { result ->
                                val pointsEarned = result.points.toDoubleOrNull() ?: 0.0
                                runningTotal += pointsEarned
                                RacePoints(
                                    raceName = result.raceName,
                                    round = result.round,
                                    position = result.position,
                                    points = result.points,
                                    runningTotal = runningTotal.toInt().toString()
                                )
                            }
                            isLoadingResults = false
                        }
                    }
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

    // Show loading dialog while fetching results
    if (isLoadingResults) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Loading race results...") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            },
            confirmButton = { }
        )
    }

    // Show points breakdown dialog
    if (!isLoadingResults && selectedDriver != null) {
        val driver = selectedDriver!!
        val teamColor = driver.constructors.firstOrNull()?.let {
            TeamColors.getTeamColor(it.constructorId)
        } ?: Color.Gray

        DriverPointsBreakdownDialog(
            driver = driver.driver,
            teamColor = teamColor,
            racePoints = driverResults,
            totalPoints = driver.points,
            onDismiss = {
                selectedDriver = null
                driverResults = emptyList()
            }
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
                text = "DRIVERS CHAMPIONSHIP",
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
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = CountryFlags.getFlag(standing.driver.nationality),
                        style = MaterialTheme.typography.titleMedium
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
