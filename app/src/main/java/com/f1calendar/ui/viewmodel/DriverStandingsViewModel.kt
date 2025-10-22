package com.f1calendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.f1calendar.data.model.DriverStanding
import com.f1calendar.data.repository.F1Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DriverStandingsUiState {
    object Loading : DriverStandingsUiState()
    data class Success(val standings: List<DriverStanding>) : DriverStandingsUiState()
    data class Error(val message: String) : DriverStandingsUiState()
}

class DriverStandingsViewModel(
    private val repository: F1Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DriverStandingsUiState>(DriverStandingsUiState.Loading)
    val uiState: StateFlow<DriverStandingsUiState> = _uiState.asStateFlow()

    init {
        loadStandings()
    }

    fun loadStandings() {
        viewModelScope.launch {
            _uiState.value = DriverStandingsUiState.Loading
            repository.getDriverStandings().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { standings -> DriverStandingsUiState.Success(standings) },
                    onFailure = { error -> DriverStandingsUiState.Error(error.message ?: "Unknown error") }
                )
            }
        }
    }

    fun refresh() {
        loadStandings()
    }

    suspend fun getDriverResults(driverId: String): List<F1Repository.DriverRaceResult> {
        var results = emptyList<F1Repository.DriverRaceResult>()
        repository.getDriverSeasonResults(driverId).collect { result ->
            result.onSuccess { results = it }
        }
        return results
    }
}
