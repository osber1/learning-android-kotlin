package io.osvaldas.weatherapp.models

import java.io.Serializable

data class ForecastTimestamp(
    val forecastTimeUTC: String,
    val airTemperature: Double,
    val windSpeed: Long,
    val windGust: Long,
    val windDirection: Long,
    val cloudCover: Long,
    val seaLevelPressure: Long,
    val relativeHumidity: Long,
    val totalPrecipitation: Double,
    val conditionCode: String
) : Serializable