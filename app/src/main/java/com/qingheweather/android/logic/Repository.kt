package com.qingheweather.android.logic

import androidx.lifecycle.liveData
import com.qingheweather.android.logic.dao.PlaceDao
import com.qingheweather.android.logic.model.Place
import com.qingheweather.android.logic.model.Weather
import com.qingheweather.android.logic.network.QingheWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = QingheWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String, placeName: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                QingheWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                QingheWeatherNetwork.getDailyWeather(lng, lat)
            }
            val deferredHourly = async {
                QingheWeatherNetwork.getHourlyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            val hourlyResponse = deferredHourly.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok" && hourlyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily, hourlyResponse.result.hourly)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}" +
                                     "hourly response status is ${hourlyResponse.status}"
                    )
                )
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

}