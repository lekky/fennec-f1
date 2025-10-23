package com.f1calendar.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.f1calendar.data.model.QualifyingResult
import com.f1calendar.data.model.Race
import com.f1calendar.data.model.RaceResult
import com.f1calendar.ui.theme.F1Gold
import com.f1calendar.ui.viewmodel.RaceDetailViewModel
import com.f1calendar.ui.viewmodel.SessionResultsUiState
import com.f1calendar.util.CountryFlags
import com.f1calendar.util.DateTimeUtil
import com.f1calendar.util.TeamColors
import coil.compose.AsyncImage

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
                title = {
                    Text(
                        if (selectedSession != null) "$selectedSession Results"
                        else race.raceName
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedSession != null) {
                                selectedSession = null
                            } else {
                                onBackClick()
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                            "SprintShootout" -> viewModel.loadQualifyingResults(race.round) // Sprint shootout uses qualifying format
                            "Practice1", "Practice2", "Practice3" -> {
                                // Practice sessions - API may not have detailed results
                                // Load as race results to show positions/times if available
                                viewModel.loadRaceResults(race.round)
                            }
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = CountryFlags.getFlag(race.circuit.location.country),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = race.circuit.circuitName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${race.circuit.location.locality}, ${race.circuit.location.country}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Round ${race.round} • ${DateTimeUtil.formatDateRange(race.date)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    // Track map view
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TRACK LAYOUT",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TrackMapView(
                                circuitId = race.circuit.circuitId,
                                trackColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                            )
                        }
                    }
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

        // Check if this is a sprint weekend
        val isSprint = race.sprint != null

        if (isSprint) {
            // Sprint weekend order: FP1, Qualifying (for race), Sprint Shootout, Sprint, Race
            // Note: Sprint Shootout may not be in API data

            // Practice 1
            race.firstPractice?.let { session ->
                item {
                    SessionCard(
                        name = "Practice 1",
                        date = session.date,
                        time = session.time,
                        isCompleted = isCompleted,
                        onClick = if (isCompleted) { { onSessionClick("Practice1") } } else null
                    )
                }
            }

            // Qualifying (for the main race - happens on Friday for sprint weekends)
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

            // Sprint Shootout (may not be in API - using sprint session as indicator)
            race.sprint?.let { session ->
                // Show a placeholder for Sprint Shootout if we're before the sprint
                item {
                    SessionCard(
                        name = "Sprint Shootout",
                        date = session.date,
                        time = session.time,
                        isCompleted = isCompleted,
                        onClick = if (isCompleted) { { onSessionClick("SprintShootout") } } else null
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
        } else {
            // Normal weekend: FP1, FP2, FP3, Qualifying, Race

            // Practice 1
            race.firstPractice?.let { session ->
                item {
                    SessionCard(
                        name = "Practice 1",
                        date = session.date,
                        time = session.time,
                        isCompleted = isCompleted,
                        onClick = if (isCompleted) { { onSessionClick("Practice1") } } else null
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
                        onClick = if (isCompleted) { { onSessionClick("Practice2") } } else null
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
                        onClick = if (isCompleted) { { onSessionClick("Practice3") } } else null
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
        }

        // Race (always last)
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

@Composable
private fun TrackMapView(
    circuitId: String,
    trackColor: Color,
    backgroundColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val width = size.width
        val height = size.height
        val strokeWidth = 8f

        val path = getCircuitPath(circuitId, width, height)

        // Draw track background (wider)
        drawPath(
            path = path,
            color = backgroundColor,
            style = Stroke(
                width = strokeWidth + 16f,
                cap = StrokeCap.Round
            )
        )

        // Draw track line
        drawPath(
            path = path,
            color = trackColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        // Draw start/finish line
        drawLine(
            color = trackColor,
            start = Offset(width * 0.1f, height * 0.45f),
            end = Offset(width * 0.1f, height * 0.55f),
            strokeWidth = strokeWidth * 1.5f,
            cap = StrokeCap.Round
        )

        // Draw checkered pattern on start/finish
        val checkSize = 4f
        for (i in 0..3) {
            val y = height * 0.45f + (i * checkSize * 2)
            drawCircle(
                color = if (i % 2 == 0) Color.White else trackColor,
                radius = checkSize,
                center = Offset(width * 0.1f - checkSize, y)
            )
            drawCircle(
                color = if (i % 2 == 0) trackColor else Color.White,
                radius = checkSize,
                center = Offset(width * 0.1f + checkSize, y)
            )
        }
    }
}

private fun getCircuitPath(circuitId: String, width: Float, height: Float): Path {
    return Path().apply {
        when (circuitId.lowercase()) {
            "monaco" -> {
                // Monaco - tight street circuit with hairpin
                moveTo(width * 0.15f, height * 0.4f)
                lineTo(width * 0.3f, height * 0.4f)
                cubicTo(width * 0.35f, height * 0.4f, width * 0.38f, height * 0.35f, width * 0.42f, height * 0.3f)
                cubicTo(width * 0.48f, height * 0.22f, width * 0.55f, height * 0.2f, width * 0.62f, height * 0.25f)
                lineTo(width * 0.72f, height * 0.32f)
                cubicTo(width * 0.78f, height * 0.37f, width * 0.8f, height * 0.45f, width * 0.82f, height * 0.55f)
                cubicTo(width * 0.82f, height * 0.65f, width * 0.75f, height * 0.7f, width * 0.68f, height * 0.73f)
                cubicTo(width * 0.6f, height * 0.76f, width * 0.5f, height * 0.78f, width * 0.42f, height * 0.75f)
                cubicTo(width * 0.32f, height * 0.72f, width * 0.25f, height * 0.65f, width * 0.2f, height * 0.58f)
                cubicTo(width * 0.16f, height * 0.52f, width * 0.14f, height * 0.46f, width * 0.15f, height * 0.4f)
            }
            "silverstone" -> {
                // Silverstone - fast flowing corners
                moveTo(width * 0.1f, height * 0.5f)
                lineTo(width * 0.25f, height * 0.5f)
                cubicTo(width * 0.32f, height * 0.5f, width * 0.38f, height * 0.45f, width * 0.45f, height * 0.38f)
                cubicTo(width * 0.52f, height * 0.3f, width * 0.6f, height * 0.28f, width * 0.68f, height * 0.32f)
                cubicTo(width * 0.76f, height * 0.36f, width * 0.82f, height * 0.44f, width * 0.85f, height * 0.53f)
                cubicTo(width * 0.87f, height * 0.62f, width * 0.85f, height * 0.7f, width * 0.78f, height * 0.75f)
                cubicTo(width * 0.7f, height * 0.8f, width * 0.6f, height * 0.82f, width * 0.5f, height * 0.8f)
                cubicTo(width * 0.38f, height * 0.78f, width * 0.28f, height * 0.72f, width * 0.2f, height * 0.64f)
                cubicTo(width * 0.13f, height * 0.58f, width * 0.1f, height * 0.54f, width * 0.1f, height * 0.5f)
            }
            "spa" -> {
                // Spa - long fast circuit with Eau Rouge
                moveTo(width * 0.1f, height * 0.6f)
                lineTo(width * 0.25f, height * 0.6f)
                cubicTo(width * 0.3f, height * 0.6f, width * 0.33f, height * 0.5f, width * 0.36f, height * 0.4f)
                cubicTo(width * 0.39f, height * 0.3f, width * 0.45f, height * 0.25f, width * 0.55f, height * 0.25f)
                lineTo(width * 0.7f, height * 0.25f)
                cubicTo(width * 0.78f, height * 0.25f, width * 0.84f, height * 0.3f, width * 0.88f, height * 0.38f)
                cubicTo(width * 0.9f, height * 0.48f, width * 0.88f, height * 0.58f, width * 0.82f, height * 0.66f)
                cubicTo(width * 0.75f, height * 0.74f, width * 0.65f, height * 0.78f, width * 0.55f, height * 0.8f)
                cubicTo(width * 0.42f, height * 0.82f, width * 0.3f, height * 0.8f, width * 0.2f, height * 0.75f)
                cubicTo(width * 0.13f, height * 0.7f, width * 0.1f, height * 0.65f, width * 0.1f, height * 0.6f)
            }
            "monza" -> {
                // Monza - high speed with chicanes
                moveTo(width * 0.15f, height * 0.5f)
                lineTo(width * 0.35f, height * 0.5f)
                cubicTo(width * 0.4f, height * 0.5f, width * 0.43f, height * 0.45f, width * 0.45f, height * 0.4f)
                cubicTo(width * 0.47f, height * 0.37f, width * 0.5f, height * 0.36f, width * 0.53f, height * 0.38f)
                cubicTo(width * 0.56f, height * 0.4f, width * 0.58f, height * 0.43f, width * 0.6f, height * 0.45f)
                lineTo(width * 0.75f, height * 0.45f)
                cubicTo(width * 0.82f, height * 0.45f, width * 0.87f, height * 0.52f, width * 0.88f, height * 0.6f)
                cubicTo(width * 0.87f, height * 0.68f, width * 0.82f, height * 0.73f, width * 0.75f, height * 0.75f)
                cubicTo(width * 0.6f, height * 0.78f, width * 0.45f, height * 0.77f, width * 0.32f, height * 0.72f)
                cubicTo(width * 0.22f, height * 0.68f, width * 0.15f, height * 0.6f, width * 0.15f, height * 0.5f)
            }
            "suzuka" -> {
                // Suzuka - figure-8 layout
                moveTo(width * 0.1f, height * 0.55f)
                lineTo(width * 0.25f, height * 0.55f)
                cubicTo(width * 0.35f, height * 0.55f, width * 0.42f, height * 0.48f, width * 0.48f, height * 0.4f)
                cubicTo(width * 0.54f, height * 0.32f, width * 0.62f, height * 0.3f, width * 0.7f, height * 0.35f)
                cubicTo(width * 0.77f, height * 0.4f, width * 0.8f, height * 0.48f, width * 0.78f, height * 0.56f)
                cubicTo(width * 0.76f, height * 0.64f, width * 0.7f, height * 0.7f, width * 0.62f, height * 0.72f)
                cubicTo(width * 0.52f, height * 0.74f, width * 0.42f, height * 0.72f, width * 0.34f, height * 0.68f)
                cubicTo(width * 0.24f, height * 0.63f, width * 0.16f, height * 0.58f, width * 0.1f, height * 0.55f)
            }
            "interlagos" -> {
                // Interlagos - elevation changes and curves
                moveTo(width * 0.12f, height * 0.5f)
                lineTo(width * 0.28f, height * 0.5f)
                cubicTo(width * 0.35f, height * 0.5f, width * 0.4f, height * 0.42f, width * 0.42f, height * 0.33f)
                cubicTo(width * 0.44f, height * 0.26f, width * 0.5f, height * 0.22f, width * 0.57f, height * 0.24f)
                cubicTo(width * 0.65f, height * 0.26f, width * 0.72f, height * 0.32f, width * 0.78f, height * 0.4f)
                cubicTo(width * 0.84f, height * 0.5f, width * 0.86f, height * 0.6f, width * 0.82f, height * 0.69f)
                cubicTo(width * 0.77f, height * 0.77f, width * 0.68f, height * 0.82f, width * 0.58f, height * 0.83f)
                cubicTo(width * 0.45f, height * 0.84f, width * 0.33f, height * 0.8f, width * 0.24f, height * 0.72f)
                cubicTo(width * 0.16f, height * 0.64f, width * 0.12f, height * 0.57f, width * 0.12f, height * 0.5f)
            }
            else -> {
                // Default generic circuit for unknown tracks
                moveTo(width * 0.1f, height * 0.5f)
                lineTo(width * 0.35f, height * 0.5f)
                cubicTo(width * 0.45f, height * 0.5f, width * 0.5f, height * 0.6f, width * 0.5f, height * 0.7f)
                cubicTo(width * 0.5f, height * 0.8f, width * 0.6f, height * 0.85f, width * 0.75f, height * 0.8f)
                cubicTo(width * 0.85f, height * 0.77f, width * 0.88f, height * 0.65f, width * 0.85f, height * 0.55f)
                cubicTo(width * 0.82f, height * 0.45f, width * 0.75f, height * 0.35f, width * 0.65f, height * 0.3f)
                cubicTo(width * 0.5f, height * 0.23f, width * 0.3f, height * 0.25f, width * 0.15f, height * 0.35f)
                cubicTo(width * 0.08f, height * 0.4f, width * 0.08f, height * 0.45f, width * 0.1f, height * 0.5f)
            }
        }
    }
}
