package com.f1calendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.f1calendar.data.model.ConstructorStanding
import com.f1calendar.data.repository.F1Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ConstructorStandingsUiState {
    object Loading : ConstructorStandingsUiState()
    data class Success(val standings: List<ConstructorStanding>) : ConstructorStandingsUiState()
    data class Error(val message: String) : ConstructorStandingsUiState()
}

class ConstructorStandingsViewModel(
    private val repository: F1Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConstructorStandingsUiState>(ConstructorStandingsUiState.Loading)
    val uiState: StateFlow<ConstructorStandingsUiState> = _uiState.asStateFlow()

    init {
        loadStandings()
    }

    fun loadStandings() {
        viewModelScope.launch {
            _uiState.value = ConstructorStandingsUiState.Loading
            repository.getConstructorStandings().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { standings -> ConstructorStandingsUiState.Success(standings) },
                    onFailure = { error -> ConstructorStandingsUiState.Error(error.message ?: "Unknown error") }
                )
            }
        }
    }

    fun refresh() {
        loadStandings()
    }

    suspend fun getConstructorResults(constructorId: String): List<F1Repository.ConstructorRaceResult> {
        var results = emptyList<F1Repository.ConstructorRaceResult>()
        repository.getConstructorSeasonResults(constructorId).collect { result ->
            result.onSuccess { results = it }
        }
        return results
    }
}
