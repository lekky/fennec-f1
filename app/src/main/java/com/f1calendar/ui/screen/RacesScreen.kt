package com.f1calendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(races) { race ->
            RaceCard(race = race, onClick = { onRaceClick(race) })
        }
    }
}

@Composable
private fun RaceCard(
    race: Race,
    onClick: () -> Unit
) {
    val isCompleted = DateTimeUtil.isRaceCompleted(race.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏁 ${race.raceName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF4CAF50)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Upcoming",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = race.circuit.circuitName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = "📍 ${race.circuit.location.locality}, ${race.circuit.location.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Round ${race.round}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = DateTimeUtil.formatDateRange(race.date),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            if (!isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                    race.sprint?.let {
                        SessionTimeText("Sprint", it.date, it.time)
                    }
                    race.time?.let {
                        SessionTimeText("Race", race.date, it)
                    }
                }
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
