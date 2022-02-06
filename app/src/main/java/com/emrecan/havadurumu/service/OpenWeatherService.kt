package com.emrecan.havadurumu.service

import com.emrecan.havadurumu.service.dto.CurrentWeather
import com.emrecan.havadurumu.service.dto.FullWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {

    @GET("weather?units=metric&lang=tr")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") appid: String,
    ): Response<CurrentWeather>

    @GET("onecall?units=metric&exclude=current,hourly,alerts")
    suspend fun getFullWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") appid: String,
    ): Response<FullWeather>

    @GET("onecall?units=metric&exclude=current,alerts")
    suspend fun getHourlyWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") appid: String,
    ): Response<FullWeather>
}