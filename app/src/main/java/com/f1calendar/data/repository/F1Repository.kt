package com.f1calendar.data.repository

import com.f1calendar.data.local.dao.RaceDao
import com.f1calendar.data.model.*
import com.f1calendar.data.remote.F1ApiService
import com.f1calendar.data.remote.dto.RaceResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

class F1Repository(
    private val apiService: F1ApiService,
    private val raceDao: RaceDao
) {

    fun getRaces(): Flow<Result<List<Race>>> = flow {
        try {
            // Emit cached data first
            raceDao.getRacesBySeason(getCurrentSeason()).collect { cachedRaces ->
                if (cachedRaces.isNotEmpty()) {
                    emit(Result.success(cachedRaces))
                }
            }

            // Fetch fresh data
            val response = apiService.getSeasonRaces(getCurrentSeason())
            val races = response.mrData.raceTable?.races?.map { it.toRace() } ?: emptyList()

            // Cache the data
            raceDao.deleteRacesBySeason(getCurrentSeason())
            raceDao.insertRaces(races)

            emit(Result.success(races))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getRaceResults(round: Int): Flow<Result<List<RaceResult>>> = flow {
        try {
            val response = apiService.getRaceResults(getCurrentSeason(), round)
            val results = response.mrData.raceTable?.races?.firstOrNull()?.results ?: emptyList()
            emit(Result.success(results))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getQualifyingResults(round: Int): Flow<Result<List<QualifyingResult>>> = flow {
        try {
            val response = apiService.getQualifyingResults(getCurrentSeason(), round)
            val results = response.mrData.raceTable?.races?.firstOrNull()?.qualifyingResults ?: emptyList()
            emit(Result.success(results))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getSprintResults(round: Int): Flow<Result<List<RaceResult>>> = flow {
        try {
            val response = apiService.getSprintResults(getCurrentSeason(), round)
            val results = response.mrData.raceTable?.races?.firstOrNull()?.sprintResults ?: emptyList()
            emit(Result.success(results))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getDriverStandings(): Flow<Result<List<DriverStanding>>> = flow {
        try {
            val response = apiService.getDriverStandings(getCurrentSeason())
            val standings = response.mrData.standingsTable?.standingsLists?.firstOrNull()?.driverStandings ?: emptyList()
            emit(Result.success(standings))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getConstructorStandings(): Flow<Result<List<ConstructorStanding>>> = flow {
        try {
            val response = apiService.getConstructorStandings(getCurrentSeason())
            val standings = response.mrData.standingsTable?.standingsLists?.firstOrNull()?.constructorStandings ?: emptyList()
            emit(Result.success(standings))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun getCurrentSeason(): String {
        return Calendar.getInstance().get(Calendar.YEAR).toString()
    }

    private fun RaceResponse.toRace(): Race {
        return Race(
            round = round.toInt(),
            raceName = raceName,
            circuit = circuit,
            date = date,
            time = time,
            firstPractice = firstPractice,
            secondPractice = secondPractice,
            thirdPractice = thirdPractice,
            qualifying = qualifying,
            sprint = sprint,
            season = season
        )
    }
}
