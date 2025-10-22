package com.f1calendar.data.remote

import com.f1calendar.data.remote.dto.ApiResponse
import com.f1calendar.data.remote.dto.RaceTable
import com.f1calendar.data.remote.dto.StandingsTable
import retrofit2.http.GET
import retrofit2.http.Path

interface F1ApiService {

    @GET("f1/{season}/races")
    suspend fun getSeasonRaces(
        @Path("season") season: String = "current"
    ): ApiResponse<RaceTable>

    @GET("f1/{season}/{round}/results")
    suspend fun getRaceResults(
        @Path("season") season: String,
        @Path("round") round: Int
    ): ApiResponse<RaceTable>

    @GET("f1/{season}/{round}/qualifying")
    suspend fun getQualifyingResults(
        @Path("season") season: String,
        @Path("round") round: Int
    ): ApiResponse<RaceTable>

    @GET("f1/{season}/{round}/sprint")
    suspend fun getSprintResults(
        @Path("season") season: String,
        @Path("round") round: Int
    ): ApiResponse<RaceTable>

    @GET("f1/{season}/driverstandings")
    suspend fun getDriverStandings(
        @Path("season") season: String = "current"
    ): ApiResponse<StandingsTable>

    @GET("f1/{season}/constructorstandings")
    suspend fun getConstructorStandings(
        @Path("season") season: String = "current"
    ): ApiResponse<StandingsTable>

    @GET("f1/{season}/drivers/{driverId}/results")
    suspend fun getDriverResults(
        @Path("season") season: String,
        @Path("driverId") driverId: String
    ): ApiResponse<RaceTable>

    @GET("f1/{season}/constructors/{constructorId}/results")
    suspend fun getConstructorResults(
        @Path("season") season: String,
        @Path("constructorId") constructorId: String
    ): ApiResponse<RaceTable>

    @GET("f1/{season}/results")
    suspend fun getAllSeasonResults(
        @Path("season") season: String = "current"
    ): ApiResponse<RaceTable>
}
