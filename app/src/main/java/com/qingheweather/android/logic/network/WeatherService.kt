package com.qingheweather.android.logic.network

import com.qingheweather.android.QingheWeatherApplication
import com.qingheweather.android.logic.model.DailyResponse
import com.qingheweather.android.logic.model.HourlyResponse
import com.qingheweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.5/${QingheWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<RealtimeResponse>

    @GET("v2.5/${QingheWeatherApplication.TOKEN}/{lng},{lat}/daily?dailysteps=7")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>

    @GET("v2.5/${QingheWeatherApplication.TOKEN}/{lng},{lat}/hourly?hourlysteps=24")
    fun getHourlyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<HourlyResponse>

}