package com.f1calendar.data.repository

import com.f1calendar.data.local.dao.RaceDao
import com.f1calendar.data.model.*
import com.f1calendar.data.remote.F1ApiService
import com.f1calendar.data.remote.dto.RaceResponse
import com.f1calendar.util.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Calendar

class F1Repository(
    private val apiService: F1ApiService,
    private val raceDao: RaceDao
) {

    fun getRaces(): Flow<Result<List<Race>>> = flow {
        try {
            val season = getCurrentSeason()
            AppLogger.i("F1Repository", "getRaces() - Starting, season: $season")

            // Emit cached data first
            val cachedRaces = raceDao.getRacesBySeason(season).first()
            AppLogger.d("F1Repository", "getRaces() - Found ${cachedRaces.size} cached races")
            if (cachedRaces.isNotEmpty()) {
                emit(Result.success(cachedRaces))
            }

            // Fetch fresh data
            AppLogger.d("F1Repository", "getRaces() - Fetching from API for season: $season")
            val response = apiService.getSeasonRaces(season)
            AppLogger.d("F1Repository", "getRaces() - API response received")

            val races = response.mrData.raceTable?.races?.map { it.toRace() } ?: emptyList()
            AppLogger.i("F1Repository", "getRaces() - Parsed ${races.size} races from API")

            // Cache the data
            raceDao.deleteRacesBySeason(season)
            raceDao.insertRaces(races)
            AppLogger.d("F1Repository", "getRaces() - Cached races to database")

            emit(Result.success(races))
            AppLogger.i("F1Repository", "getRaces() - Successfully emitted ${races.size} races")
        } catch (e: Exception) {
            AppLogger.e("F1Repository", "getRaces() - Error: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    fun getRaceResults(round: Int): Flow<Result<List<RaceResult>>> = flow {
        try {
            AppLogger.d("F1Repository", "getRaceResults() - Fetching round $round")
            val response = apiService.getRaceResults(getCurrentSeason(), round)
            val results = response.mrData.raceTable?.races?.firstOrNull()?.results ?: emptyList()
            AppLogger.i("F1Repository", "getRaceResults() - Got ${results.size} results")
            emit(Result.success(results))
        } catch (e: Exception) {
            AppLogger.e("F1Repository", "getRaceResults() - Error: ${e.message}", e)
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
            AppLogger.d("F1Repository", "getDriverStandings() - Fetching")
            val response = apiService.getDriverStandings(getCurrentSeason())
            val standings = response.mrData.standingsTable?.standingsLists?.firstOrNull()?.driverStandings ?: emptyList()
            AppLogger.i("F1Repository", "getDriverStandings() - Got ${standings.size} drivers")
            emit(Result.success(standings))
        } catch (e: Exception) {
            AppLogger.e("F1Repository", "getDriverStandings() - Error: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    fun getConstructorStandings(): Flow<Result<List<ConstructorStanding>>> = flow {
        try {
            AppLogger.d("F1Repository", "getConstructorStandings() - Fetching")
            val response = apiService.getConstructorStandings(getCurrentSeason())
            val standings = response.mrData.standingsTable?.standingsLists?.firstOrNull()?.constructorStandings ?: emptyList()
            AppLogger.i("F1Repository", "getConstructorStandings() - Got ${standings.size} constructors")
            emit(Result.success(standings))
        } catch (e: Exception) {
            AppLogger.e("F1Repository", "getConstructorStandings() - Error: ${e.message}", e)
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
