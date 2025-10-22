package com.f1calendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f1calendar.data.model.Race
import com.f1calendar.ui.theme.F1Gold
import com.f1calendar.ui.viewmodel.RacesUiState
import com.f1calendar.ui.viewmodel.RacesViewModel
import com.f1calendar.util.CountryFlags
import com.f1calendar.util.DateTimeUtil

@Composable
fun RacesScreen(
    viewModel: RacesViewModel,
    onRaceClick: (Race) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is RacesUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is RacesUiState.Success -> {
            RacesList(races = state.races, onRaceClick = onRaceClick)
        }

        is RacesUiState.Error -> {
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

@Composable
private fun RacesList(
    races: List<Race>,
    onRaceClick: (Race) -> Unit
) {
    val upcomingRaces = races.filter { !DateTimeUtil.isRaceCompleted(it.date) }
    val completedRaces = races.filter { DateTimeUtil.isRaceCompleted(it.date) }
    var completedExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Upcoming Races Section
        if (upcomingRaces.isNotEmpty()) {
            item {
                Text(
                    text = "UPCOMING RACES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(upcomingRaces) { race ->
                RaceCard(race = race, onClick = { onRaceClick(race) })
            }
        }

        // Completed Races Section
        if (completedRaces.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { completedExpanded = !completedExpanded },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "COMPLETED RACES",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${completedRaces.size} races",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = if (completedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (completedExpanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            if (completedExpanded) {
                items(completedRaces) { race ->
                    RaceCard(race = race, onClick = { onRaceClick(race) })
                }
            }
        }
    }
}

@Composable
private fun RaceCard(
    race: Race,
    onClick: () -> Unit
) {
    val isCompleted = DateTimeUtil.isRaceCompleted(race.date)
    val flagColor = CountryFlags.getFlag(race.circuit.location.country)

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
            verticalAlignment = Alignment.Top
        ) {
            // Country flag color indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Round number badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = race.round.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Race info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = flagColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = race.raceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = race.circuit.circuitName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = "${race.circuit.location.locality}, ${race.circuit.location.country}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = DateTimeUtil.formatDateRange(race.date),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Show session times for upcoming races
                if (!isCompleted) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        // Check if this is a sprint weekend
                        val isSprint = race.sprint != null

                        if (isSprint) {
                            // Sprint weekend order
                            race.firstPractice?.let {
                                SessionTimeText("FP1", it.date, it.time)
                            }
                            race.qualifying?.let {
                                SessionTimeText("Sprint Qualifying", it.date, it.time)
                            }
                            race.secondPractice?.let {
                                SessionTimeText("FP2", it.date, it.time)
                            }
                            race.sprint?.let {
                                SessionTimeText("Sprint", it.date, it.time)
                            }
                            race.thirdPractice?.let {
                                SessionTimeText("FP3", it.date, it.time)
                            }
                            // Main qualifying happens after sprint on sprint weekends (if there's a separate session)
                            race.time?.let {
                                SessionTimeText("Race", race.date, it)
                            }
                        } else {
                            // Normal weekend order
                            race.firstPractice?.let {
                                SessionTimeText("FP1", it.date, it.time)
                            }
                            race.secondPractice?.let {
                                SessionTimeText("FP2", it.date, it.time)
                            }
                            race.thirdPractice?.let {
                                SessionTimeText("FP3", it.date, it.time)
                            }
                            race.qualifying?.let {
                                SessionTimeText("Qualifying", it.date, it.time)
                            }
                            race.time?.let {
                                SessionTimeText("Race", race.date, it)
                            }
                        }
                    }
                }
            }

            // Status icon
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Upcoming",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SessionTimeText(name: String, date: String, time: String?) {
    Text(
        text = "$name: ${DateTimeUtil.formatToUKTime(date, time)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    )
}
