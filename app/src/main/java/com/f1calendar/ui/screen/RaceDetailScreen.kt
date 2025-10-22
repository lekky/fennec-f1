package com.f1calendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f1calendar.data.model.QualifyingResult
import com.f1calendar.data.model.Race
import com.f1calendar.data.model.RaceResult
import com.f1calendar.ui.theme.F1Gold
import com.f1calendar.ui.viewmodel.RaceDetailViewModel
import com.f1calendar.ui.viewmodel.SessionResultsUiState
import com.f1calendar.util.DateTimeUtil
import com.f1calendar.util.TeamColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceDetailScreen(
    race: Race,
    viewModel: RaceDetailViewModel,
    onBackClick: () -> Unit
) {
    var selectedSession by remember { mutableStateOf<String?>(null) }
    val isCompleted = DateTimeUtil.isRaceCompleted(race.date)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(race.raceName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedSession != null) {
                SessionResultsView(
                    viewModel = viewModel,
                    sessionName = selectedSession!!,
                    onBack = { selectedSession = null }
                )
            } else {
                RaceDetailsView(
                    race = race,
                    isCompleted = isCompleted,
                    onSessionClick = { session ->
                        selectedSession = session
                        when (session) {
                            "Race" -> viewModel.loadRaceResults(race.round)
                            "Qualifying" -> viewModel.loadQualifyingResults(race.round)
                            "Sprint" -> viewModel.loadSprintResults(race.round)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RaceDetailsView(
    race: Race,
    isCompleted: Boolean,
    onSessionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = race.circuit.circuitName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "📍 ${race.circuit.location.locality}, ${race.circuit.location.country}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Round ${race.round} • ${DateTimeUtil.formatDateRange(race.date)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            Text(
                text = "📊 SESSIONS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Practice 1
        race.firstPractice?.let { session ->
            item {
                SessionCard(
                    name = "Practice 1",
                    date = session.date,
                    time = session.time,
                    isCompleted = isCompleted,
                    onClick = if (isCompleted) null else null // Practice results not typically shown
                )
            }
        }

        // Practice 2
        race.secondPractice?.let { session ->
            item {
                SessionCard(
                    name = "Practice 2",
                    date = session.date,
                    time = session.time,
                    isCompleted = isCompleted,
                    onClick = null
                )
            }
        }

        // Practice 3
        race.thirdPractice?.let { session ->
            item {
                SessionCard(
                    name = "Practice 3",
                    date = session.date,
                    time = session.time,
                    isCompleted = isCompleted,
                    onClick = null
                )
            }
        }

        // Qualifying
        race.qualifying?.let { session ->
            item {
                SessionCard(
                    name = "Qualifying",
                    date = session.date,
                    time = session.time,
                    isCompleted = isCompleted,
                    onClick = if (isCompleted) { { onSessionClick("Qualifying") } } else null
                )
            }
        }

        // Sprint
        race.sprint?.let { session ->
            item {
                SessionCard(
                    name = "Sprint",
                    date = session.date,
                    time = session.time,
                    isCompleted = isCompleted,
                    onClick = if (isCompleted) { { onSessionClick("Sprint") } } else null
                )
            }
        }

        // Race
        race.time?.let { time ->
            item {
                SessionCard(
                    name = "🏆 RACE",
                    date = race.date,
                    time = time,
                    isCompleted = isCompleted,
                    isMainRace = true,
                    onClick = if (isCompleted) { { onSessionClick("Race") } } else null
                )
            }
        }
    }
}

@Composable
private fun SessionCard(
    name: String,
    date: String,
    time: String?,
    isCompleted: Boolean,
    isMainRace: Boolean = false,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = if (isMainRace) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
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
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = DateTimeUtil.formatToUKTime(date, time),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (isCompleted && onClick != null) {
                Text(
                    text = "View Results →",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SessionResultsView(
    viewModel: RaceDetailViewModel,
    sessionName: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back to Sessions")
        }

        when (val state = uiState) {
            is SessionResultsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SessionResultsUiState.RaceSuccess -> {
                RaceResultsList(
                    results = state.results,
                    sessionName = sessionName
                )
            }

            is SessionResultsUiState.QualifyingSuccess -> {
                QualifyingResultsList(results = state.results)
            }

            is SessionResultsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: ${state.message}")
                }
            }
        }
    }
}

@Composable
private fun RaceResultsList(
    results: List<RaceResult>,
    sessionName: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "$sessionName Results",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(results) { result ->
            RaceResultCard(result = result)
        }
    }
}

@Composable
private fun RaceResultCard(result: RaceResult) {
    val teamColor = TeamColors.getTeamColor(result.constructor.constructorId)
    val hasFastestLap = result.fastestLap?.rank == "1"

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(teamColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Position with medal for podium
            Text(
                text = when (result.position.toIntOrNull()) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> result.position
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Driver info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = result.number,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = teamColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${result.driver.givenName} ${result.driver.familyName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = result.constructor.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                result.time?.let {
                    Text(
                        text = it.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                if (result.status != "Finished") {
                    Text(
                        text = result.status,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }

            // Fastest lap indicator
            if (hasFastestLap) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(F1Gold.copy(alpha = 0.2f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "⚡",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "FASTEST",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = F1Gold
                    )
                }
            }
        }
    }
}

@Composable
private fun QualifyingResultsList(results: List<QualifyingResult>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Qualifying Results",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(results) { result ->
            QualifyingResultCard(result = result)
        }
    }
}

@Composable
private fun QualifyingResultCard(result: QualifyingResult) {
    val teamColor = TeamColors.getTeamColor(result.constructor.constructorId)

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(teamColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Position
            Text(
                text = result.position,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Driver info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = result.number,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = teamColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${result.driver.givenName} ${result.driver.familyName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = result.constructor.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Qualifying times
            Column(horizontalAlignment = Alignment.End) {
                result.q3?.let {
                    Text(
                        text = "Q3: $it",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                result.q2?.let {
                    Text(
                        text = "Q2: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                result.q1?.let {
                    Text(
                        text = "Q1: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
