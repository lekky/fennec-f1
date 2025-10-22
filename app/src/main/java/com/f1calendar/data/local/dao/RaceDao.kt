package com.f1calendar.data.local.dao

import androidx.room.*
import com.f1calendar.data.model.Race
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceDao {

    @Query("SELECT * FROM races WHERE season = :season ORDER BY round ASC")
    fun getRacesBySeason(season: String): Flow<List<Race>>

    @Query("SELECT * FROM races WHERE season = :season AND round = :round")
    fun getRaceByRound(season: String, round: Int): Flow<Race?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaces(races: List<Race>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRace(race: Race)

    @Query("DELETE FROM races WHERE season = :season")
    suspend fun deleteRacesBySeason(season: String)

    @Query("DELETE FROM races")
    suspend fun deleteAllRaces()
}
