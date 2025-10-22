package com.f1calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.f1calendar.data.model.Race
import com.f1calendar.ui.navigation.AppNavigation
import com.f1calendar.ui.navigation.Screen
import com.f1calendar.ui.screen.ConstructorStandingsScreen
import com.f1calendar.ui.screen.DriverStandingsScreen
import com.f1calendar.ui.screen.LogViewerDialog
import com.f1calendar.ui.screen.RacesScreen
import com.f1calendar.ui.theme.F1CalendarTheme
import com.f1calendar.ui.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var racesViewModel: RacesViewModel
    private lateinit var driverStandingsViewModel: DriverStandingsViewModel
    private lateinit var constructorStandingsViewModel: ConstructorStandingsViewModel
    private lateinit var raceDetailViewModel: RaceDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get repository from application
        val app = application as F1Application
        val repository = app.repository

        // Initialize ViewModels
        racesViewModel = RacesViewModel(repository)
        driverStandingsViewModel = DriverStandingsViewModel(repository)
        constructorStandingsViewModel = ConstructorStandingsViewModel(repository)
        raceDetailViewModel = RaceDetailViewModel(repository)

        setContent {
            F1CalendarTheme {
                F1CalendarApp()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun F1CalendarApp() {
        var currentRace by remember { mutableStateOf<Race?>(null) }
        var showLogViewer by remember { mutableStateOf(false) }
        val navController = rememberNavController()
        val tabs = listOf("Races", "Drivers", "Constructors")
        val pagerState = rememberPagerState(pageCount = { tabs.size })
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("F1 2025") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showLogViewer = true }
                ) {
                    Icon(Icons.Filled.BugReport, contentDescription = "View Logs")
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                AppNavigation(
                    navController = navController,
                    raceDetailViewModel = raceDetailViewModel,
                    currentRace = currentRace
                ) {
                    Column {
                        // Tab Row
                        TabRow(selectedTabIndex = pagerState.currentPage) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    text = { Text(title) }
                                )
                            }
                        }

                        // Swipeable Tab Content
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> RacesScreen(
                                    viewModel = racesViewModel,
                                    onRaceClick = { race ->
                                        currentRace = race
                                        navController.navigate(
                                            Screen.RaceDetail.createRoute(race.round, race.raceName)
                                        )
                                    }
                                )

                                1 -> DriverStandingsScreen(viewModel = driverStandingsViewModel)
                                2 -> ConstructorStandingsScreen(viewModel = constructorStandingsViewModel)
                            }
                        }
                    }
                }
            }
        }

        // Log viewer dialog
        if (showLogViewer) {
            LogViewerDialog(onDismiss = { showLogViewer = false })
        }
    }
}
