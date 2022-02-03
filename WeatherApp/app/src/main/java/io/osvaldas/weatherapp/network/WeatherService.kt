package io.osvaldas.weatherapp.network

import io.osvaldas.weatherapp.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    @GET("places/{city}/forecasts/long-term")
    fun getWeather(@Path("city") city: String): Call<WeatherResponse>
}