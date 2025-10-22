package com.f1calendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.f1calendar.data.model.QualifyingResult
import com.f1calendar.data.model.RaceResult
import com.f1calendar.data.repository.F1Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SessionResultsUiState {
    object Loading : SessionResultsUiState()
    data class RaceSuccess(val results: List<RaceResult>) : SessionResultsUiState()
    data class QualifyingSuccess(val results: List<QualifyingResult>) : SessionResultsUiState()
    data class Error(val message: String) : SessionResultsUiState()
}

class RaceDetailViewModel(
    private val repository: F1Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SessionResultsUiState>(SessionResultsUiState.Loading)
    val uiState: StateFlow<SessionResultsUiState> = _uiState.asStateFlow()

    fun loadRaceResults(round: Int) {
        viewModelScope.launch {
            _uiState.value = SessionResultsUiState.Loading
            repository.getRaceResults(round).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { results -> SessionResultsUiState.RaceSuccess(results) },
                    onFailure = { error -> SessionResultsUiState.Error(error.message ?: "Unknown error") }
                )
            }
        }
    }

    fun loadQualifyingResults(round: Int) {
        viewModelScope.launch {
            _uiState.value = SessionResultsUiState.Loading
            repository.getQualifyingResults(round).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { results -> SessionResultsUiState.QualifyingSuccess(results) },
                    onFailure = { error -> SessionResultsUiState.Error(error.message ?: "Unknown error") }
                )
            }
        }
    }

    fun loadSprintResults(round: Int) {
        viewModelScope.launch {
            _uiState.value = SessionResultsUiState.Loading
            repository.getSprintResults(round).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { results -> SessionResultsUiState.RaceSuccess(results) },
                    onFailure = { error -> SessionResultsUiState.Error(error.message ?: "Unknown error") }
                )
            }
        }
    }
}
