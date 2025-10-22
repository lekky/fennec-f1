package com.f1calendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.f1calendar.data.model.Race
import com.f1calendar.data.repository.F1Repository
import com.f1calendar.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RacesUiState {
    object Loading : RacesUiState()
    data class Success(val races: List<Race>) : RacesUiState()
    data class Error(val message: String) : RacesUiState()
}

class RacesViewModel(
    private val repository: F1Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RacesUiState>(RacesUiState.Loading)
    val uiState: StateFlow<RacesUiState> = _uiState.asStateFlow()

    init {
        loadRaces()
    }

    fun loadRaces() {
        viewModelScope.launch {
            AppLogger.i("RacesViewModel", "loadRaces() - Starting")
            _uiState.value = RacesUiState.Loading
            repository.getRaces().collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { races ->
                        AppLogger.i("RacesViewModel", "loadRaces() - Success with ${races.size} races")
                        RacesUiState.Success(races)
                    },
                    onFailure = { error ->
                        AppLogger.e("RacesViewModel", "loadRaces() - Failed: ${error.message}", error)
                        RacesUiState.Error(error.message ?: "Unknown error")
                    }
                )
            }
        }
    }

    fun refresh() {
        loadRaces()
    }
}
