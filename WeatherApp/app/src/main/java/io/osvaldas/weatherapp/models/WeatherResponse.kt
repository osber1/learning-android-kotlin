package io.osvaldas.weatherapp.models

import java.io.Serializable

data class WeatherResponse(
    val place: Place,
    val forecastType: String,
    val forecastCreationTimeUTC: String,
    val forecastTimestamps: List<ForecastTimestamp>
) : Serializable

